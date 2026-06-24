package com.ferisooo.kawaiilogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Writes log lines to per-day files in plugins/KawaiiLogger/logs/.
 * Writes are queued on a single background daemon thread.
 */
public final class LogWriter {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** How often the buffered writer is flushed to disk. */
    private static final long FLUSH_INTERVAL_SECONDS = 2L;

    private final File logsDir;
    private final Logger pluginLog;
    private final ExecutorService exec;
    private final ScheduledExecutorService flusher;

    private LocalDate currentDate;
    private BufferedWriter currentWriter;

    public LogWriter(File logsDir, Logger pluginLog) {
        this.logsDir = logsDir;
        this.pluginLog = pluginLog;
        if (!logsDir.exists()) logsDir.mkdirs();
        this.exec = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "KawaiiLogger-FileWriter");
            t.setDaemon(true);
            return t;
        });
        this.flusher = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "KawaiiLogger-FileFlusher");
            t.setDaemon(true);
            return t;
        });
        // Periodically flush the buffered writer instead of flushing per line.
        // The flush runs on the same single writer thread to avoid concurrent
        // access to currentWriter.
        this.flusher.scheduleWithFixedDelay(
                () -> exec.submit(this::flush),
                FLUSH_INTERVAL_SECONDS, FLUSH_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void log(String category, String message) {
        final LocalDateTime now = LocalDateTime.now();
        final String line = "[" + TIME_FMT.format(now) + "] [" + category + "] " + sanitize(message);
        exec.submit(() -> writeLine(now.toLocalDate(), line));
    }

    private void writeLine(LocalDate date, String line) {
        try {
            if (currentWriter == null || currentDate == null || !currentDate.equals(date)) {
                rollover(date);
            }
            if (currentWriter != null) {
                currentWriter.write(line);
                currentWriter.newLine();
                // No per-line flush: a scheduled task flushes periodically and
                // shutdown() performs a final flush, so lines are not lost.
            }
        } catch (IOException ex) {
            if (pluginLog != null) pluginLog.warning("(\u2727) log write failed: " + ex.getMessage());
        }
    }

    /** Flushes the buffered writer. Runs on the single writer thread. */
    private void flush() {
        if (currentWriter != null) {
            try {
                currentWriter.flush();
            } catch (IOException ex) {
                if (pluginLog != null) pluginLog.warning("(\u2727) log flush failed: " + ex.getMessage());
            }
        }
    }

    private void rollover(LocalDate date) throws IOException {
        if (currentWriter != null) {
            try { currentWriter.close(); } catch (IOException ignored) {}
            currentWriter = null;
        }
        File f = new File(logsDir, date.toString() + ".log");
        currentWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8));
        currentDate = date;
    }

    private static String sanitize(String s) {
        if (s == null) return "";
        // strip newlines so each event is one line
        return s.replace('\n', ' ').replace('\r', ' ');
    }

    public void shutdown() {
        // Stop the periodic flusher first so it can't submit new tasks after
        // the writer executor has been shut down.
        flusher.shutdownNow();
        // Submit a final flush, then let queued writes drain.
        exec.submit(this::flush);
        exec.shutdown();
        try {
            if (!exec.awaitTermination(2, TimeUnit.SECONDS)) {
                exec.shutdownNow();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        if (currentWriter != null) {
            // close() flushes any remaining buffered data.
            try { currentWriter.close(); } catch (IOException ignored) {}
            currentWriter = null;
        }
    }
}

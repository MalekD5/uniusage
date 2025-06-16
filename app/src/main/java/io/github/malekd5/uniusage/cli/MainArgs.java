package io.github.malekd5.uniusage.cli;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class MainArgs {

    @Parameter(names = "--logfile", description = "Path to log file", required = true, converter = FileConverter.class)
    private File logFile;

    public File getLogFile() {
        return logFile;
    }

}

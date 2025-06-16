package io.github.malekd5.uniusage.cli;

import java.util.Map;

import com.beust.jcommander.JCommander;

import io.github.malekd5.uniusage.cli.commands.CountCommand;
import io.github.malekd5.uniusage.cli.commands.TopOperationsCommand;

public class CommandMap {

    private JCommander commanderInstance;

    private final Map<String, Runnable> commands;

    public CommandMap() {
        this.commands = Map.of(
                "top", new TopOperationsCommand(),
                "count", new CountCommand());

        initJCommander();
    }

    private void initJCommander() {
        JCommander.Builder builder = JCommander.newBuilder()
                .programName("UniUsage");

        for (var entry : commands.entrySet()) {
            builder.addCommand(entry.getKey(), entry.getValue());
        }

        this.commanderInstance = builder.build();
    }

    public JCommander getCommanderInstance() {
        return this.commanderInstance;
    }

    public void execute(String... argv) {
        commanderInstance.parse(argv);
        String parsedCommand = commanderInstance.getParsedCommand();

        if (parsedCommand == null || parsedCommand.isEmpty()) {
            commanderInstance.usage();
            return;
        }

        final Runnable command = this.commands.get(parsedCommand.toLowerCase());

        if (command == null) {
            System.out.println("Invalid command: " + parsedCommand);
            commanderInstance.usage();
            return;
        }
        command.run();
    }

}

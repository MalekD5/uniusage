package io.github.malekd5.uniusage.cli;

import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;

import io.github.malekd5.uniusage.cli.commands.CountCommand;
import io.github.malekd5.uniusage.cli.commands.TopCommand;
import io.github.malekd5.uniusage.cli.commands.TopHLLOperationsCommand;

public class CommandMap {

    private JCommander commanderInstance;

    private final Map<String, Runnable> commands;

    public CommandMap() {
        this.commands = Map.of(
                "tophll", new TopHLLOperationsCommand(),
                "top", new TopCommand(),
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
        try {
            commanderInstance.parse(argv);
        } catch (MissingCommandException e) {
            System.out.println("Unknown command");
            commanderInstance.usage();
            return;
        }
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

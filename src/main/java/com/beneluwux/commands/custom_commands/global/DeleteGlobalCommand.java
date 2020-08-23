package com.beneluwux.commands.custom_commands.global;

import com.beneluwux.helper.EmbedHelper;
import com.beneluwux.meta.CustomCommandComponent;
import com.beneluwux.models.command.Command;
import com.beneluwux.models.command.CommandArgument;
import com.beneluwux.models.command.CommandArgumentType;
import com.beneluwux.models.command.CommandParameter;
import com.beneluwux.models.entities.CustomCommand;
import com.beneluwux.repositories.CustomCommandRepository;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteGlobalCommand extends Command {
    private final CustomCommandRepository customCommandRepository;
    private final CustomCommandComponent customCommandComponent;

    @Autowired
    public DeleteGlobalCommand(CustomCommandRepository customCommandRepository, CustomCommandComponent customCommandComponent) {
        this.commandName = "deleteglobalcommand";
        this.requiresBotOwner = true;

        this.commandArguments.add(new CommandArgument("command name", "The name of the command to delete", CommandArgumentType.SingleString));
        this.customCommandRepository = customCommandRepository;
        this.customCommandComponent = customCommandComponent;
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent) {

    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, List<CommandParameter> commandParams) {
        CommandParameter commandKey = commandParams.get(0);
        CustomCommand customCommand = customCommandRepository.findByNameAndServerSnowflake((String) commandKey.getValue(), 0L);

        if (customCommand == null) {
            messageCreateEvent.getChannel().sendMessage(EmbedHelper.genericErrorEmbed("The global command `" + commandKey.getValue() + "` doesn't exist.", messageCreateEvent.getMessageAuthor().getDiscriminatedName()));
        } else {
            customCommandRepository.delete(customCommand);
            customCommandComponent.refreshCustomCommandsFromJPA();

            messageCreateEvent.getChannel().sendMessage(EmbedHelper.genericSuccessEmbed("The global command `" + commandKey.getValue() + "` has been deleted.", messageCreateEvent.getMessageAuthor().getDiscriminatedName()));
        }
    }
}
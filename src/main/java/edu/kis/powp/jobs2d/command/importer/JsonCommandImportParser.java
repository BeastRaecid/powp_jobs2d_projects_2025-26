package edu.kis.powp.jobs2d.command.importer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class JsonCommandImportParser implements CommandImportParser {
    private static final String COMMANDS_KEY = "commands";
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String X_KEY = "x";
    private static final String Y_KEY = "y";
    private static final String TYPE_SET_POSITION = "setPosition";
    private static final String TYPE_OPERATE_TO = "operateTo";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CommandImportResult parse(String text) {
        try {
            JsonNode root = objectMapper.readTree(text);
            
            if (root.isArray()) {
                return new CommandImportResult(parseCommandList(root), null);
            }
            
            if (root.isObject()) {
                if (!root.has(COMMANDS_KEY)) {
                    throw new CommandImportException("Missing 'commands' field");
                }
                
                String name = null;
                if (root.has(NAME_KEY) && root.get(NAME_KEY).isTextual()) {
                    name = root.get(NAME_KEY).asText();
                }
                
                return new CommandImportResult(parseCommandList(root.get(COMMANDS_KEY)), name);
            }
            
            throw new CommandImportException("Unsupported JSON root");
        } catch (Exception e) {
            throw new CommandImportException("JSON import failed", e);
        }
    }

    private List<DriverCommand> parseCommandList(JsonNode commandsNode) {
        if (!commandsNode.isArray()) {
            throw new CommandImportException("'commands' must be an array");
        }
        
        List<DriverCommand> commands = new ArrayList<>();
        Iterator<JsonNode> elements = commandsNode.elements();
        
        while (elements.hasNext()) {
            commands.add(parseCommand(elements.next()));
        }
        
        return commands;
    }

    private DriverCommand parseCommand(JsonNode commandNode) {
        if (!commandNode.isObject()) {
            throw new CommandImportException("Command entry must be an object");
        }

        if (!commandNode.has(TYPE_KEY) || !commandNode.get(TYPE_KEY).isTextual()) {
             throw new CommandImportException("Field '" + TYPE_KEY + "' must be a string");
        }

        String type = commandNode.get(TYPE_KEY).asText();

        if (TYPE_SET_POSITION.equals(type)) {
            int x = readRequiredInt(commandNode, X_KEY);
            int y = readRequiredInt(commandNode, Y_KEY);
            return new SetPositionCommand(x, y);
        }
        
        if (TYPE_OPERATE_TO.equals(type)) {
            int x = readRequiredInt(commandNode, X_KEY);
            int y = readRequiredInt(commandNode, Y_KEY);
            return new OperateToCommand(x, y);
        }

        throw new CommandImportException("Unknown command type: " + type);
    }

    private int readRequiredInt(JsonNode node, String field) {
        if (!node.has(field)) {
             throw new CommandImportException("Field '" + field + "' is missing");
        }
        JsonNode valueNode = node.get(field);
        if (valueNode.isInt()) {
            return valueNode.asInt();
        }
        // Handle string-encoded integers gracefully if needed, or strictly fail.
        // For robustness, let's try parsing if it's textual.
        if (valueNode.isTextual()) {
            try {
                return Integer.parseInt(valueNode.asText());
            } catch (NumberFormatException e) {
                throw new CommandImportException("Field '" + field + "' must be an integer");
            }
        }
        throw new CommandImportException("Field '" + field + "' must be an integer");
    }
}

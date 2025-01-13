package me.yirf.linkplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.stream.Collectors;

public class Translator {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Formats a string with MiniMessage color codes.
     *
     * @param input the input string containing MiniMessage formatting
     * @return the formatted Component
     */
    public static Component mini(String input) {
        return miniMessage.deserialize(input);
    }

    /**
     * Formats a list of strings with MiniMessage color codes.
     *
     * @param inputs the list of strings containing MiniMessage formatting
     * @return a list of formatted Components
     */
    public static List<Component> mini(List<String> inputs) {
        return inputs.stream()
                .map(miniMessage::deserialize)
                .collect(Collectors.toList());
    }

    public static Component replacePlaceholder(Component component, String placeholder, String replacement) {
        return component.replaceText(TextReplacementConfig.builder()
                .matchLiteral(placeholder) // Match the exact placeholder
                .replacement(replacement) // Replace it with the specified text
                .build());
    }
}

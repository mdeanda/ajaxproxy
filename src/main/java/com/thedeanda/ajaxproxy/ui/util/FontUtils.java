package com.thedeanda.ajaxproxy.ui.util;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class FontUtils {
    public static Font getFont(Font defaultFont, String fontName, float size) {
        Set<String> wanted = new HashSet<>();
        wanted.add(fontName);
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font detailsFont = Stream.of(e.getAllFonts())
                //.peek(f -> log.warn(f.getName()))
                .filter(f -> wanted.contains(f.getName()))
                .findFirst()
                .map(f -> f.deriveFont(size))
                .orElse(defaultFont);

        return detailsFont;
    }
}

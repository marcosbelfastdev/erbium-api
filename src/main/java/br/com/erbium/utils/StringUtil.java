package br.com.erbium.utils;

import lombok.NonNull;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

public class StringUtil {

    public static void print(@NonNull String... messages) {
        for (String message : messages) {
            System.out.print(message);
        }
    }

    public static void printErbiumLogo() {
        String erbium = """
                88888888888  88888888ba   88888888ba   88  88        88  88b           d88               \s
                88           88      "8b  88      "8b  88  88        88  888b         d888               \s
                88           88      ,8P  88      ,8P  88  88        88  88`8b       d8'88               \s
                88aaaaa      88aaaaaa8P'  88aaaaaa8P'  88  88        88  88 `8b     d8' 88               \s
                88""\"""      88""\""88'    88""\"""\"8b,  88  88        88  88  `8b   d8'  88               \s
                88           88    `8b    88      `8b  88  88        88  88   `8b d8'   88               \s
                88           88     `8b   88      a8P  88  Y8a.    .a8P  88    `888'    88               \s
                88888888888  88      `8b  88888888P"   88   `"Y8888Y"'   88     `8'     88\s
                Testing Framework
                """;
        System.out.println(erbium);
    }
}

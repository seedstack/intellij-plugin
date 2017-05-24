package org.seedstack.intellij.config.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroResolver {
    private static final Pattern MACRO_PATTERN = Pattern.compile("\\\\?\\$\\{|\\}");

    public List<Match> resolve(String value) {
        return resolve(value, 0);
    }

    private List<Match> resolve(String value, int startIndex) {
        List<Match> matches = new ArrayList<>();
        int currentPos = startIndex;
        Match matchingResult;
        while ((matchingResult = findMatchingCurlyBraces(value, currentPos)) != null) {
            if (!matchingResult.escaped) {
                int offset = startIndex;
                for (String part : value.substring(matchingResult.startPos, matchingResult.endPos).split(":")) {
                    offset += 2;
                    if (part.startsWith("'") && part.endsWith("'")) {
                        break;
                    } else {
                        matches.addAll(resolve(value, offset));
                    }
                }
            }
            matches.add(matchingResult);
            currentPos = matchingResult.endPos + 1;
        }
        return matches;
    }

    private Match findMatchingCurlyBraces(String value, int startIndex) {
        int level = 0, startPos = -1;
        boolean escaped = false;
        Matcher matcher = MACRO_PATTERN.matcher(value);
        while (matcher.find()) {
            if (matcher.start() < startIndex) {
                continue;
            }
            switch (matcher.group()) {
                case "\\${":
                    if (level == 0) {
                        escaped = true;
                    }
                case "${":
                    if (level == 0) {
                        startPos = matcher.start();
                    }
                    level++;
                    break;
                case "}":
                    level--;
                    if (level == 0) {
                        return new Match(value, startPos + 2, matcher.start(), escaped);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected string in macro " + matcher.group());
            }
        }
        return null;
    }

    public static class Match {
        private final String value;
        private final int startPos;
        private final int endPos;
        private final boolean escaped;

        Match(String value, int startPos, int endPos, boolean escaped) {
            this.value = value;
            this.startPos = startPos;
            this.endPos = endPos;
            this.escaped = escaped;
        }

        public int getStartPos() {
            return startPos;
        }

        public int getEndPos() {
            return endPos;
        }

        public boolean isEscaped() {
            return escaped;
        }

        public String getReference() {
            return value.substring(startPos, endPos);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Match match = (Match) o;
            return startPos == match.startPos && endPos == match.endPos && escaped == match.escaped && value.equals(match.value);
        }

        @Override
        public int hashCode() {
            int result = value.hashCode();
            result = 31 * result + startPos;
            result = 31 * result + endPos;
            result = 31 * result + (escaped ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Match{" +
                    "value='" + value + '\'' +
                    ", startPos=" + startPos +
                    ", endPos=" + endPos +
                    ", escaped=" + escaped +
                    '}';
        }
    }
}

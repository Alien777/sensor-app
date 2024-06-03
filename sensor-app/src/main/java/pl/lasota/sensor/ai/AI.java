package pl.lasota.sensor.ai;

import lombok.AllArgsConstructor;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@AllArgsConstructor(staticName = "create")
public class AI {

    public boolean matchText(String source, List<List<String>> tokenizerTarget) {
        List<String> tokenizer = tokenizer(source);
        return match(tokenizer, tokenizerTarget);
    }

    public static List<String> tokenizer(String source) {
        String normalized = Normalizer.normalize(source, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String ascii = pattern.matcher(normalized).replaceAll("");
        String lowerCase = ascii.toLowerCase();
        return Arrays.stream(lowerCase.split("\\s+")).filter(s -> s.length() > 3).toList();
    }

    private static boolean match(List<String> queue, List<List<String>> tokenizerTarget) {
        return tokenizerTarget.stream().anyMatch(s -> {
            for (String target : s) {
                if (!queue.contains(target)) {
                    return false;
                }
            }
            return true;
        });

    }
}

package tests;

import java.util.*;
import core.SkillGapAnalyzer;
import core.ReccomendationEngine;

public class SkillGapAnalyzerTest {
    public static void main(String[] args) {
        List<String> user = Arrays.asList("java", "git", "docker");
        List<String> required = Arrays.asList("aws", "docker", "java", "react", "sql");

        List<String> missing = SkillGapAnalyzer.computeMissingSkills(required, user);
        List<String> expectedMissing = Arrays.asList("aws", "react", "sql");

        assert missing.equals(expectedMissing) : "❌ Missing skills test failed";

        List<String> ordered = ReccomendationEngine.prioritize(new ArrayList<>(missing));
        List<String> expectedOrder = Arrays.asList("sql", "aws", "react");

        assert ordered.equals(expectedOrder) : "❌ Recommendation order test failed";

        System.out.println("✅ Test 1 passed!");

        List<String> userAll = Arrays.asList("java", "aws", "docker", "react", "sql");
        List<String> requiredAll = Arrays.asList("aws", "docker", "java", "react", "sql");

        List<String> missingAll = SkillGapAnalyzer.computeMissingSkills(requiredAll, userAll);
        List<String> expectedNone = new ArrayList<>();

        assert missingAll.equals(expectedNone) : "❌ Test 2: Should have no missing skills";

        System.out.println("✅ Test 2 passed!");

        List<String> badExpected = Arrays.asList("react", "aws", "sql"); 
        assert ordered.equals(badExpected) : "❌ Test 3: Expected failure triggered";

        System.out.println("✅ Test 3 passed!");
    }
}

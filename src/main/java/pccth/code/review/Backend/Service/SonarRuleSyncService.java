package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.SonarRuleSyncRequestDTO;
import pccth.code.review.Backend.Entity.SonarRuleSecurityEntity;
import pccth.code.review.Backend.Repository.SonarRuleSecurityRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SonarRuleSyncService {

    private final SonarRuleSecurityRepository repo;

    public SonarRuleSyncService(SonarRuleSecurityRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public int sync(SonarRuleSyncRequestDTO req) {
        Map<String, SonarRuleSecurityEntity> map = new HashMap<>();

        invert(req.getOwasp(), map, true);
        invert(req.getSecurity(), map, false);

        Date now = new Date();
        for (SonarRuleSecurityEntity e : map.values()) {
            e.setUpdatedAt(now);
        }

        repo.saveAll(map.values());
        return map.size();
    }

    private void invert(Map<String, List<String>> catToRules,
                        Map<String, SonarRuleSecurityEntity> out,
                        boolean isOwasp) {
        if (catToRules == null) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : catToRules.entrySet()) {
            String category = entry.getKey();
            List<String> ruleKeys = entry.getValue();
            if (category == null || ruleKeys == null) {
                continue;
            }

            for (String ruleKey : ruleKeys) {
                if (ruleKey == null || ruleKey.isBlank()) {
                    continue;
                }

                SonarRuleSecurityEntity e = out.computeIfAbsent(ruleKey, k -> {
                    SonarRuleSecurityEntity n = new SonarRuleSecurityEntity();
                    n.setRuleKey(k);
                    n.setOwaspCategories(new ArrayList<>());
                    n.setSecurityCategories(new ArrayList<>());
                    return n;
                });

                List<String> list = isOwasp ? e.getOwaspCategories() : e.getSecurityCategories();
                if (!list.contains(category)) {
                    list.add(category);
                }
            }
        }
    }
}

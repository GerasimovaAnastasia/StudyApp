package edu.studyapp.service.factory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingStrategyFactoryImpl implements RatingStrategyFactory {
    private final ProgressRatingStrategy progressStrategy;
    private final GradeRatingStrategy gradeStrategy;
    private Map<String, RatingStrategy<?>> strategies;
    private static final Logger logger = LoggerFactory.getLogger(RatingStrategyFactoryImpl.class);

    @PostConstruct
    public void init() {
        strategies = Map.of(
                "progress", progressStrategy,
                "grade", gradeStrategy
        );
    }

    @Override
    public RatingStrategy<?> getStrategy(String type) {
        String normalizedType = type.toLowerCase();
        if (!strategies.containsKey(normalizedType)) {
            String errorMessage = String.format("Недоступный тип стратегии: %s; доступные типы: %s", type, strategies.keySet());
            logger.warn("{}: {}", "getStrategy", errorMessage);
            throw new IllegalArgumentException(
                    errorMessage
            );
        }
        return strategies.get(normalizedType);
    }
}

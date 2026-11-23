package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseworkTest {

    // 1. Контрольний розрахунок інтегрального показника: 0.40*52 + 0.35*60 + 0.25*70 = 59.30
    @Test
    void integralRiskIndexMatchesFormula() {
        double expected = 59.30;
        double actual = RiskCalculator.calculate(52.0, 60.0, 70.0);
        assertEquals(expected, actual, 0.0001);
    }

    // 2. Граничне нижнє значення: усі фактори по 0 дають інтегральний показник 0.0
    @Test
    void zeroFactorsProduceZeroRiskIndex() {
        double expected = 0.0;
        double actual = RiskCalculator.calculate(0.0, 0.0, 0.0);
        assertEquals(expected, actual, 0.0001);
    }

    // 3. Граничне верхнє значення: усі фактори по 100 дають інтегральний показник 100.0
    @Test
    void maxFactorsProduceHundredRiskIndex() {
        double expected = 100.0;
        double actual = RiskCalculator.calculate(100.0, 100.0, 100.0);
        assertEquals(expected, actual, 0.0001);
    }

    // 4. Відхилення першого фактора вище 100
    @Test
    void factorF1AboveMaxRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                RiskCalculator.calculate(101.0, 20.0, 30.0)
        );
    }

    // 5. Відхилення від'ємного значення фактора
    @Test
    void negativeFactorRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                RiskCalculator.calculate(20.0, -5.0, 30.0)
        );
    }

    // 6. Дозволений перехід стану: ВИЯВЛЕНО -> ПІДТВЕРДЖЕНО
    @Test
    void detectedToConfirmedAllowed() {
        assertTrue(ProblemService.isAllowedTransition(
                ProblemState.ВИЯВЛЕНО,
                ProblemState.ПІДТВЕРДЖЕНО
        ));
    }

    // 7. Дозволений перехід стану: ПІДТВЕРДЖЕНО -> ОБРОБКУ_ЗАПЛАНОВАНО
    @Test
    void confirmedToPlannedAllowed() {
        assertTrue(ProblemService.isAllowedTransition(
                ProblemState.ПІДТВЕРДЖЕНО,
                ProblemState.ОБРОБКУ_ЗАПЛАНОВАНО
        ));
    }

    // 8. Дозволений перехід стану: ОБРОБКУ_ЗАПЛАНОВАНО -> УСУНЕНО
    @Test
    void plannedToResolvedAllowed() {
        assertTrue(ProblemService.isAllowedTransition(
                ProblemState.ОБРОБКУ_ЗАПЛАНОВАНО,
                ProblemState.УСУНЕНО
        ));
    }

    // 9. Заборонений перехід стану (стрибок через стани): ВИЯВЛЕНО -> УСУНЕНО
    @Test
    void detectedToResolvedRejected() {
        assertFalse(ProblemService.isAllowedTransition(
                ProblemState.ВИЯВЛЕНО,
                ProblemState.УСУНЕНО
        ));
    }

    // 10. Валідація сутності Field: порожня назва та від'ємна площа відхиляються до звернення до БД
    @Test
    void invalidFieldDataRejected() {
        FieldService service = new FieldService();
        Field invalidField = new Field(null, "   ", -10.0, "Пшениця");
        assertThrows(IllegalArgumentException.class, () -> service.add(invalidField));
    }
}
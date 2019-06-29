package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_O3;
import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_TEMPERATURA;
import static org.junit.Assert.assertTrue;

public class MeasurementProcessorTest {

    @Test
    public void test_processing_of_raw_data_not_to_process() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        RawMeasurement rawMeasurement = new RawMeasurement(MEASUREMENT_ID_TEMPERATURA, 1.2);
        AugeG4RawData rawData = new AugeG4RawData("AIRQ01", new Date(), Collections.singletonList(rawMeasurement));

        // when
        Optional<ProcessedMeasurement> processedMeasurementContainer = measurementProcessor.process(rawData, rawMeasurement);

        // then
        assertTrue(processedMeasurementContainer.isPresent());
    }

    @Test
    public void test_processing_of_raw_to_process() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        RawMeasurement temperatura = new RawMeasurement(MEASUREMENT_ID_TEMPERATURA, 1.2);
        RawMeasurement O3 = new RawMeasurement(MEASUREMENT_ID_O3, 2.3);
        AugeG4RawData rawData = new AugeG4RawData("AIRQ01", new Date(), Arrays.asList(temperatura, O3));

        // when
        Optional<ProcessedMeasurement> processedMeasurementContainer = measurementProcessor.process(rawData, O3);

        // then
        assertTrue(processedMeasurementContainer.isPresent());
    }

    @Test
    public void test_correct_processing() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        // when
        // then
    }

    @Test
    public void ignore_failed_processing_of_raw_data_due_to_missing_measurement_used_in_complex_formula() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        RawMeasurement O3 = new RawMeasurement(MEASUREMENT_ID_O3, 2.3);
        AugeG4RawData rawData = new AugeG4RawData("AIRQ01", new Date(), Collections.singletonList(O3));

        // when
        Optional<ProcessedMeasurement> processedMeasurementContainer = measurementProcessor.process(rawData, O3);

        // then
        assertTrue(processedMeasurementContainer.isPresent());
    }


    @Test
    public void bigDecimal_compute() {
        double amount = 100.05;
        BigDecimal big_amount= new BigDecimal("100.05");
        double discount = amount * 0.10;
        MathContext mathContext = new MathContext(5,RoundingMode.HALF_UP);
        BigDecimal big_discount = big_amount.multiply(new BigDecimal("0.10"),mathContext);
        double total = amount - discount;
        BigDecimal big_total = big_amount.subtract(big_discount,mathContext);
        double tax = total * 0.05;
        BigDecimal big_tax = big_total.multiply(new BigDecimal("0.05"),mathContext);
        double taxedTotal = tax + total;
        BigDecimal big_taxedTotal = big_tax.add(big_total,mathContext);

        NumberFormat money = NumberFormat.getCurrencyInstance();
        System.out.println("Subtotal : "+ money.format(amount));
        System.out.println("Discount : " + money.format(discount));
        System.out.println("Total : " + money.format(total));
        System.out.println("Tax : " + money.format(tax));
        System.out.println("Tax+Total: " + money.format(taxedTotal));

        System.out.println("-----------");
        System.out.println("Subtotal : "+ money.format(big_amount));
        System.out.println("Discount : " + money.format(big_discount));
        System.out.println("Total : " + money.format(big_total));
        System.out.println("Tax : " + money.format(big_tax));
        System.out.println("Tax+Total: " + money.format(big_taxedTotal));
    }

    @Test
    public void test_apply_function() {
        MeasurementProcessor processor = new MeasurementProcessor();


        BigDecimal a = new BigDecimal("-0.45");
        BigDecimal b = new BigDecimal("103.3");
        BigDecimal c = new BigDecimal("9.67");
        BigDecimal d = new BigDecimal("-0.00009452");
        BigDecimal e = new BigDecimal("-5687.74");
        BigDecimal f = new BigDecimal("0");
        BigDecimal rad = new BigDecimal("1");

        caso(processor, a, b, c, d, e, f, rad, "91", "87", "23.1", -21.44d);

        caso(processor, a, b, c, d, e, f, rad, "87", "87", "23.2", -114.49d);

        caso(processor, a, b, c, d, e, f, rad, "85", "87", "23.3", -166.66d);

        caso(processor, a, b, c, d, e, f, rad, "86", "87", "23.2", -139.89d);

        caso(processor, a, b, c, d, e, f, rad, "83", "87", "23.2", -221.49d);

        caso(processor, a, b, c, d, e, f, rad, "96", "87", "23.3", 73.09d);

        caso(processor, a, b, c, d, e, f, rad, "88", "88", "23.7", -92.43d);
    }

    private void caso(MeasurementProcessor processor, BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d, BigDecimal e, BigDecimal f, BigDecimal rad, String s_no2, String s_o3, String s_t, double v_expected) {
        BigDecimal no2;
        BigDecimal o3;
        BigDecimal t;
        double v;
        no2 = new BigDecimal(s_no2);
        o3 = new BigDecimal(s_o3);
        t = new BigDecimal(s_t);
        v = processor.applyFunction(no2, a, b, c, d, e, f, o3, t, rad);
        System.out.println("v=" + v);
        System.out.println("v_expected:" + v_expected);
        double v_diff = v - v_expected;
        System.out.println(v_diff);
        assertTrue(Math.abs(v_diff) < 0.5);
        System.out.println();
    }
}

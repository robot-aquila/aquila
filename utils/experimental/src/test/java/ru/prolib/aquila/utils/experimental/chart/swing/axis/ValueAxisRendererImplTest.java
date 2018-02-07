package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewportImpl;

public class ValueAxisRendererImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testX() {
		//CDecimal dLength = CDecimalBD.of(200L);
		//CDecimal dMinValueStep = CDecimalBD.of("0.01");
		//CDecimal dMinValue = CDecimalBD.of("467.35");
		//CDecimal dMaxValue = CDecimalBD.of("15480.36");
		//CDecimal dMinStep = CDecimalBD.of(20L);
		// expected firstVal=2000, lastVal=14000, stepValue=2000

		
		CDecimal dLength = CDecimalBD.of(200L);
		CDecimal dMinValueStep = CDecimalBD.of("0.01");
		CDecimal dMinValue = CDecimalBD.of("100.02");
		CDecimal dMaxValue = CDecimalBD.of("643.17");
		CDecimal dMinStep = CDecimalBD.of(30L);

		
		double length = dLength.toBigDecimal().doubleValue();
		int precision = dMinValueStep.getScale();
		double minValue = dMinValue.toBigDecimal().doubleValue();
		double maxValue = dMaxValue.toBigDecimal().doubleValue();
		double minStep = dMinStep.toBigDecimal().doubleValue();

        length = Math.abs(length);
        double firstVal = 0d, lastVal = 0d, stepValue = 0d;
        int stepCount;

        double minValueStep = dMinValueStep.toBigDecimal().doubleValue();
        System.out.println("minValueStep=" + minValueStep);
        System.out.println("dMinValueStep=" + dMinValueStep);
        
        double range = maxValue - minValue;
        CDecimal dRange = dMaxValue.subtract(dMinValue);
        System.out.println("range=" + range);
        System.out.println("dRange=" + dRange);
        if(range > 0){
            stepCount = new Double(length/minStep).intValue();
            System.out.println("stepCount[base]=" + stepCount);
            stepValue = -1;
            while (stepValue < minValueStep && stepCount > 0){
            	System.out.println(">>>iteration");
            	// Что здесь может быть, какая точность нужна?
            	// Если range очень маленький, то результат будет еще меньше.
            	// Нужно взять scale от range и добавить к нему еще что то.
                stepValue = range/stepCount;
                // mul это округленный десятичный множитель. Он используется
                // для выравнивания меток по значениям кратным степени десятки
                // для удобства восприятия пользователем.
                // Как его рассчитать альтернативно, не используя log10?
                double mul = Math.pow(10, Math.floor(Math.log10(stepValue)));
                double v = stepValue / mul;
                
                CDecimal dStepCount = CDecimalBD.of((long)stepCount);
                CDecimal dStepValue = dRange.divideExact(dStepCount, 8 + precision, RoundingMode.HALF_UP);
                
                System.out.println("stepValue=" + stepValue);
                System.out.println("dStepValue=" + dStepValue);
                
                
                CDecimal dMul = getMul(dStepValue);
                System.out.println("mul=" + mul);
                System.out.println("dMul=" + dMul);

                CDecimal dV = dStepValue.divide(dMul);
                
                System.out.println("v=" + v);
                System.out.println("dV=" + dV);
                
                System.out.println("log10(" + stepValue + ")=" + Math.log10(stepValue));
                System.out.println("floor(log10(" + stepValue + "))=" + Math.floor(Math.log10(stepValue)));
                if(v < 2d){
                    v = 2d;
                } else if(v < 5d){
                    v = 5d;
                } else {
                    v = 10d;
                }
                if ( dV.compareTo(CDecimalBD.of(1L)) <= 0 ) {
                	dV = CDecimalBD.of(1L);
                } else if ( dV.compareTo(CDecimalBD.of(2L)) <= 0 ) {
                	dV = CDecimalBD.of(2L);
                } else if ( dV.compareTo(CDecimalBD.of(5L)) <= 0 ) {
                	dV = CDecimalBD.of(5L);
                } else {
                	dV = CDecimalBD.of(10L);
                }
                
                System.out.println("v[fixed]=" + v);
                System.out.println("dV[fixed]=" + dV);
                
                stepValue = v * mul;
                dStepValue = dV.multiply(dMul).withScale(precision);
                System.out.println("stepValue[fixed]=" + stepValue);
                System.out.println("dStepValue[fixed]=" + dStepValue);
                
                if(stepValue < minValueStep){
                    stepCount--;
                    System.out.println("stepValue is less than minValueStep -> reduce stepCount to " + stepCount);
                }
            }
            firstVal = Math.ceil(minValue/stepValue)*stepValue;
            lastVal = Math.floor(maxValue/stepValue)*stepValue;
            System.out.println("firstVal=" + firstVal);
            System.out.println("lastVal=" + lastVal);
        } else {
            stepCount = 0;
        }
        if(stepCount == 0){
        	// Это не будет работать, так как расширяет область значений.
            stepValue = minValueStep;
            firstVal = Math.ceil(minValue/stepValue)*stepValue - stepValue;
            lastVal = Math.floor(maxValue/stepValue)*stepValue + stepValue;
            minValue = firstVal;
            maxValue = lastVal;
            System.out.println(">>>SpecialCase");
            System.out.println("stepValue=" + stepValue);
            System.out.println("firstVal=" + firstVal);
            System.out.println("lastVal=" + lastVal);
        }

	}
	
	private CDecimal getMul(CDecimal x) {
		// x MUST be positive
		if ( x.compareTo(CDecimalBD.ZERO) <= 0 ) {
			throw new IllegalArgumentException("Argument must be greater than zero but: " + x);
		}
		BigDecimal f = x.toBigDecimal().remainder(BigDecimal.ONE);
		BigDecimal i = x.toBigDecimal().subtract(f);
		if ( i.compareTo(BigDecimal.ZERO) > 0 ) {
			String ps = i.toPlainString();
			int pp = ps.indexOf('.');
			if ( pp < 0 ) {
				pp = ps.length();
			}
			BigDecimal r = BigDecimal.TEN.pow(pp - 1);
			return new CDecimalBD(r);
		} else {
			String ps = f.toPlainString();
			int pp = StringUtils.indexOfAny(ps, "123456789");
			if ( pp < 0 ) {
				throw new IllegalStateException("Unable to find non-zero digits in fractional part: " + f);
			}
			BigDecimal r = BigDecimal.ONE.divide(BigDecimal.TEN.pow(pp - 1));
			return new CDecimalBD(r);
		}
	}
	
	@Test
	public void testX1() {
		BigDecimal x = new BigDecimal("0.002456");
		System.out.println("x=" + x + " scale=" + x.scale() + " precision=" + x.precision() + " unsc=" + x.unscaledValue());
		x = new BigDecimal("100");
		System.out.println("x=" + x + " scale=" + x.scale() + " precision=" + x.precision() + " unsc=" + x.unscaledValue());
		x = new BigDecimal("100").setScale(-1);
		System.out.println("x=" + x + " scale=" + x.scale() + " precision=" + x.precision() + " unsc=" + x.unscaledValue());
	}
	
	@Test
	public void testX2() {
		System.out.println(5 + Math.pow(10,-6));
	}
	
}

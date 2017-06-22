package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

/**
 * Created by TiM on 20.06.2017.
 */
public class RangeCalculatorImpl implements RangeCalculator {

    @Override
    public RangeInfo autoRange(double minValue, double maxValue, double length, double minStep) {
        // calculate the number of tick-marks we can fit in the given length
        int numOfTickMarks = (int)Math.floor(length/minStep);
        // can never have less than 2 tick marks one for each end
        numOfTickMarks = Math.max(numOfTickMarks, 2);

        double range = maxValue-minValue;

        // pad min and max by 2%, checking if the range is zero
        final double paddedRange = (range == 0)? minValue == 0 ? 2 : Math.abs(minValue)*0.02: Math.abs(range)*1.02;
        final double padding = (paddedRange - range) / 2;
        // if min and max are not zero then add padding to them
        double paddedMin = minValue - padding;
        double paddedMax = maxValue + padding;
        // check padding has not pushed min or max over zero line
        if ((paddedMin < 0 && minValue >= 0) || (paddedMin > 0 && minValue <= 0)) {
            // padding pushed min above or below zero so clamp to 0
            paddedMin = 0;
        }
        if ((paddedMax < 0 && maxValue >= 0) || (paddedMax > 0 && maxValue <= 0)) {
            // padding pushed min above or below zero so clamp to 0
            paddedMax = 0;
        }
        // calculate tick unit for the number of ticks can have in the given data range
        double tickUnit = paddedRange/(double)numOfTickMarks;
        // search for the best tick unit that fits
        double tickUnitRounded = 0;
        double minRounded = 0;
        double maxRounded = 0;
        int count = 0;
        double reqLength = Double.MAX_VALUE;
        String formatter = "0.00000000";
        // loop till we find a set of ticks that fit length and result in a total of less than 20 tick marks
        while (reqLength > length || count > 20) {
            int exp = (int)Math.floor(Math.log10(tickUnit));
            final double mant = tickUnit / Math.pow(10, exp);
            double ratio = mant;
            if (mant > 5d) {
                exp++;
                ratio = 1;
            } else if (mant > 1d) {
                ratio = mant > 2.5 ? 5 : 2.5;
            }
            if (exp > 1) {
                formatter = "#,##0";
            } else if (exp == 1) {
                formatter = "0";
            } else {
                final boolean ratioHasFrac = Math.rint(ratio) != ratio;
                final StringBuilder formatterB = new StringBuilder("0");
                int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);
                if (n > 0) formatterB.append(".");
                for (int i = 0; i < n; ++i) {
                    formatterB.append("0");
                }
                formatter = formatterB.toString();

            }
            tickUnitRounded = ratio * Math.pow(10, exp);
            // move min and max to nearest tick mark
            minRounded = Math.floor(paddedMin / tickUnitRounded) * tickUnitRounded;
            maxRounded = Math.ceil(paddedMax / tickUnitRounded) * tickUnitRounded;
            // calculate the required length to display the chosen tick marks for real, this will handle if there are
            // huge numbers involved etc or special formatting of the tick mark label text
            double maxReqTickGap = 0;
            double last = 0;
            count = (int)Math.ceil((maxRounded - minRounded)/tickUnitRounded);
            double major = minRounded;
            for (int i = 0; major <= maxRounded && i < count; major += tickUnitRounded, i++)  {
                double size = minStep; //~~~
                if (i == 0) { // first
                    last = size/2;
                } else {
                    maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
                }
            }
            reqLength = (count-1) * maxReqTickGap;
            tickUnit = tickUnitRounded;

            // fix for RT-35600 where a massive tick unit was being selected
            // unnecessarily. There is probably a better solution, but this works
            // well enough for now.
            if (numOfTickMarks == 2 && reqLength > length) {
                break;
            }
            if (reqLength > length || count > 20) tickUnit *= 2; // This is just for the while loop, if there are still too many ticks
        }
        double step = (maxRounded - minRounded)==0?length:tickUnitRounded*length/(maxRounded - minRounded);
        return new RangeInfo(minRounded, maxRounded, tickUnitRounded, step);
    }
}

package ru.prolib.aquila.utils.experimental.charts.layers;

/**
 * Created by TiM on 19.04.2017.
 */
public class TradeChartObject /*implements ChartLayer<Instant>*/ {

//    private final String ID_PREFIX = "TRADES";
//    private final double HEIGHT = 10d;
//    private final double WIDTH = 20d;
//
//    private Chart<Instant> chart;
//    private Map<Instant, List<TradeInfo>> data = new TreeMap<>();
//    private List<Instant> xValues = new ArrayList<>();
//    private long period = 0; //period in seconds
//
//    @Override
//    public void setChart(Chart chart) {
//        this.chart = chart;
//    }
//
//    @Override
//    public List<Instant> getCategories() {
//        return xValues;
//    }
//
//    @Override
//    public List<Node> paint() {
//        List<Node> result = new ArrayList<>();
//        int cnt = chart.getCategories().size();
//        for (int i = 0; i < cnt; i++) {
//            Instant time = chart.getCategories().get(i);
//            List<TradeInfo> tradeInfoList = data.get(time);
//            if(tradeInfoList != null && tradeInfoList.size()>0){
//                Node node = chart.getNodeById(buildId(time));
//                Group group = (Group)node;
//                if(group==null){
//                    group = new Group();
//                    group.setId(buildId(time));
//                }
//                double x = chart.getCoordByCategory(time);
//                for (int j=0; j<tradeInfoList.size(); j++){
//                    TradeInfo tradeInfo = tradeInfoList.get(j);
//                    double y = chart.getCoordByVal(tradeInfo.getPrice());
//                    double y2;
//                    String styleClass;
//                    if(tradeInfo.getAction().equals(OrderAction.BUY)){
//                        y2 = y + HEIGHT;
//                        styleClass = "trade-buy";
//                    } else {
//                        y2 = y - HEIGHT;
//                        styleClass = "trade-sell";
//                    }
//                    Polygon arrow = null;
//                    if(j<group.getChildren().size()){
//                        arrow = (Polygon) group.getChildren().get(j);
//                        arrow.getPoints().setAll(x, y, x-WIDTH/2, y2, x+WIDTH/2, y2);
//                    } else {
//                        arrow = new Polygon(x, y, x-WIDTH/2, y2, x+WIDTH/2, y2);
//                        arrow.getStyleClass().add(styleClass);
//                        group.getChildren().add(arrow);
//                    }
////                    Tooltip.install(arrow, getTooltip(tradeInfo));
//                    chart.addObjectBounds(arrow.getBoundsInParent(), getTooltipText(tradeInfo));
//                }
//                if(node==null){
//                    result.add(group);
//                }
//            }
//        }
//        return result;
//
//    }
//
//    private String getTooltipText(TradeInfo tradeInfo) {
//        return String.format("%s%n" +
//                "%s %s @ %.2f x %d%n" +
//                "ACCOUNT: %s%n" +
//                "ORDER #: %d",
//                Utils.instantToStr(tradeInfo.getTime()),
//                tradeInfo.getAction(), tradeInfo.getSymbol(), tradeInfo.getPrice(),tradeInfo.getVolume(),
//                tradeInfo.getAccount(),
//                tradeInfo.getOrderId());
//    }
//
//    private Tooltip getTooltip(TradeInfo tradeInfo){
//        return new Tooltip(getTooltipText(tradeInfo));
//    }
//
//    @Override
//    public Pair<Double, Double> getValuesInterval(List<Instant> xValues) {
//        double minY = 1e6;
//        double maxY = 0;
//        for (Instant time: xValues) {
//            List<TradeInfo> tradeInfoList = data.get(time);
//            if(tradeInfoList != null){
//                for(TradeInfo t: tradeInfoList){
//                    double y = t.getPrice();
//                    if(y > maxY){
//                        maxY = y;
//                    }
//                    if(y < minY){
//                        minY = y;
//                    }
//                }
//            }
//        }
//        return new ImmutablePair(minY, maxY);
//    }
//
//    public void setXValues(List<Instant> xValues) {
//        this.xValues.clear();
//        this.xValues.addAll(xValues);
//        if(period ==0 && xValues.size()>1){
//            setPeriod(xValues.get(0), xValues.get(1));
//        }
//    }
//
//    public void addXValue(Instant xValue) {
//        this.xValues.add(xValue);
//    }
//
//    public void setPeriod(long period) {
//        this.period = period;
//    }
//
//    public void setPeriod(Temporal from, Temporal to) {
//        this.period = ChronoUnit.SECONDS.between(from, to);
//    }
//
//    public void setPeriod(Candle candle) {
//        setPeriod(candle.getStartTime(), candle.getEndTime());
//    }
//
//    public void setData(List<TradeInfo> tradeInfoList){
//        this.data.clear();
//        addData(tradeInfoList);
//    }
//
//    public void addData(List<TradeInfo> tradeInfoList){
//        for(TradeInfo t: tradeInfoList){
//            addData(t);
//        }
//    }
//
//    public void addData(TradeInfo tradeInfo){
//        Instant tradeTime = tradeInfo.getTime();
//        for (Instant xStart : xValues) {
//            Instant xEnd = xStart.plusSeconds(period);
//            List<TradeInfo> list = data.computeIfAbsent(xStart, k -> new ArrayList<>());
//            if ((tradeTime.isAfter(xStart) && tradeTime.isBefore(xEnd)) || tradeTime.equals(xStart)) {
//                list.add(tradeInfo);
//            }
//        }
//    }
//
//    private String buildId(Instant time){
//        return ID_PREFIX+"@"+time.toString();
//    }
}

package ru.prolib.aquila.utils.experimental.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.utils.experimental.chart.axis.AxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerSpace;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRenderer;

public class ChartSpaceManagerImpl implements ChartSpaceManager {
	
	static class RulerSetup {
		private boolean visible = true;
		private int priority = 0;
		
		public RulerSetup(boolean visible, int priority) {
			this.visible = visible;
			this.priority = priority;
		}
		
		public RulerSetup() {
			this(true, 0);
		}
		
		public void setVisible(boolean visible) {
			this.visible = visible;
		}
		
		public boolean isVisible() {
			return visible;
		}
		
		public void setPriority(int priority) {
			this.priority = priority;
		}
		
		public int getPriority() {
			return priority;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != RulerSetup.class ) {
				return false;
			}
			RulerSetup o = (RulerSetup) other;
			return new EqualsBuilder()
					.append(o.priority, priority)
					.append(o.visible, visible)
					.isEquals();
		}
		
		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("visible", visible)
					.append("priority", priority)
					.toString();
		}

	}
	
	static class RulerEntry implements Comparable<RulerEntry> {
		private final ChartRulerID rulerID;
		private final int rulerPriority;
		private final int axisPriority;
		private final int rendererPriority;
		private final int positionPriority;
		
		/**
		 * This attribute is to cache rulers length value to prevent
		 * calling of renderer method twice or more times.
		 */
		private int rulerLength;
		
		public RulerEntry(ChartRulerID rulerID,
						  int rulerPriority,
						  int axisPriority,
						  int rendererPriority)
		{
			this.rulerID = rulerID;
			this.rulerPriority = rulerPriority;
			this.axisPriority = axisPriority;
			this.rendererPriority = rendererPriority;
			this.positionPriority = rulerID.isUpperPosition() ? 1 : 0;
		}
		
		public void setLength(int rulerLength) {
			this.rulerLength = rulerLength;
		}
		
		public int getLength() {
			return rulerLength;
		}
		
		public ChartRulerID getRulerID() {
			return rulerID;
		}

		@Override
		public int compareTo(RulerEntry rhs) {
			return new CompareToBuilder()
					.append(rulerPriority, rhs.rulerPriority)
					.append(axisPriority, rhs.axisPriority)
					.append(rendererPriority, rhs.rendererPriority)
					.append(positionPriority, rhs.positionPriority)
					.toComparison();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != RulerEntry.class ) {
				return false;
			}
			RulerEntry o = (RulerEntry) other;
			return new EqualsBuilder()
					.append(o.rulerID, rulerID)
					.append(o.rulerPriority, rulerPriority)
					.append(o.axisPriority, axisPriority)
					.append(o.rendererPriority, rendererPriority)
					.append(o.positionPriority, positionPriority)
					.isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(117, 29543)
					.append(rulerID)
					.append(rulerPriority)
					.append(axisPriority)
					.append(rendererPriority)
					.append(positionPriority)
					.toHashCode();
		}
		
		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("rulerID", rulerID)
					.append("rulerPrio", rulerPriority)
					.append("axisPrio", axisPriority)
					.append("rendererPrio", rendererPriority)
					.append("positionPrio", positionPriority)
					.append("rulerLength", rulerLength)
					.toString();
		}
		
	}
	
	interface LabelSizeStrategy {
		int getLabelMaxSize(RulerRenderer renderer, Object device);		
		boolean isValidAxis(AxisDriver driver);
	}

	/**
	 * Label size determination strategy of horizontal oriented space.
	 * Horizontal space means all rulers have vertical direction and
	 * we need to know their widths.
	 */
	static class HorizontalLabelSize implements LabelSizeStrategy {
		
		@Override
		public int getLabelMaxSize(RulerRenderer renderer, Object device) {
			return renderer.getMaxLabelWidth(device);
		}
		
		@Override
		public boolean isValidAxis(AxisDriver driver) {
			return driver.getAxisDirection().isVertical();
		}
		
	}
	
	/**
	 * Label size determination strategy of vertical oriented space.
	 * Vertical space means all rulers have horizontal direction and
	 * we need to know their heights.
	 */
	static class VerticalLabelSize implements LabelSizeStrategy {
		
		@Override
		public int getLabelMaxSize(RulerRenderer renderer, Object device) {
			return renderer.getMaxLabelHeight(device);
		}
		
		@Override
		public boolean isValidAxis(AxisDriver driver) {
			return driver.getAxisDirection().isHorizontal();
		}

	}
	
	private final LabelSizeStrategy labelSizeStrategy;
	private final LinkedHashMap<String, AxisDriver> drivers;
	private final HashMap<ChartRulerID, RulerSetup> rulerSetups;
	
	ChartSpaceManagerImpl(LabelSizeStrategy labelSizeStrategy,
						  LinkedHashMap<String, AxisDriver> drivers,
						  HashMap<ChartRulerID, RulerSetup> rulerSetups)
	{
		this.labelSizeStrategy = labelSizeStrategy;
		this.drivers = drivers;
		this.rulerSetups = rulerSetups;
	}
	
	public static ChartSpaceManager ofHorizontalSpace() {
		return new ChartSpaceManagerImpl(new HorizontalLabelSize(),
										 new LinkedHashMap<>(),
										 new HashMap<>());
	}
	
	public static ChartSpaceManager ofVerticalSpace() {
		return new ChartSpaceManagerImpl(new VerticalLabelSize(),
										 new LinkedHashMap<>(),
										 new HashMap<>());
	}

	@Override
	public synchronized void registerAxis(AxisDriver driver) {
		String axisID = driver.getID();
		if ( drivers.containsKey(axisID) ) {
			throw new IllegalArgumentException("Axis already registered: " + axisID);
		}
		if ( ! labelSizeStrategy.isValidAxis(driver) ) {
			throw new IllegalArgumentException("Invalid axis direction: " + axisID);
		}
		drivers.put(driver.getID(), driver);
	}

	@Override
	public synchronized void setRulerVisibility(ChartRulerID rulerID, boolean visible) {
		checkExistence(rulerID);
		RulerSetup setup = rulerSetups.get(rulerID);
		if ( setup == null ) {
			setup = new RulerSetup();
			rulerSetups.put(rulerID, setup);
		}
		setup.setVisible(visible);
	}

	@Override
	public synchronized void setRulerDisplayPriority(ChartRulerID rulerID, int priority) {
		checkExistence(rulerID);
		RulerSetup setup = rulerSetups.get(rulerID);
		if ( setup == null ) {
			setup = new RulerSetup();
			rulerSetups.put(rulerID, setup);
		}
		setup.setPriority(priority);
	}

	@Override
	public synchronized ChartSpaceLayout prepareLayout(Segment1D displaySpace,
													   Segment1D dataSpace,
													   Object device)
	{
		if ( ! displaySpace.contains(dataSpace) ) {
			throw new IllegalArgumentException("Data space is out of display space");
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized ChartSpaceLayout prepareLayout(Segment1D displaySpace,
													   int rulersMaxSpace,
													   Object device)
	{
		if ( rulersMaxSpace > displaySpace.getLength() ) {
			throw new IllegalArgumentException("Space reserved for rulers is greater than display space");
		}
		ChartRulerID rulerID = null;
		int lowerRulersLength = 0, upperRulersLength = 0;
		List<RulerEntry> includedEntries = new ArrayList<>();
		for ( RulerEntry e : buildEntries() ) {
			rulerID = e.getRulerID();
			AxisDriver driver = drivers.get(rulerID.getAxisID());
			RulerRenderer renderer = driver.getRenderer(rulerID.getRendererID());
			int rulerLength = labelSizeStrategy.getLabelMaxSize(renderer, device);
			if ( lowerRulersLength + upperRulersLength + rulerLength > rulersMaxSpace ) {
				break;
			}
			e.setLength(rulerLength);
			includedEntries.add(e);
			if ( rulerID.isLowerPosition() ) {
				lowerRulersLength += rulerLength;
			} else {
				upperRulersLength += rulerLength;
			}
		}
		int cL = displaySpace.getStart() + lowerRulersLength;
		int cU = displaySpace.getEnd() - upperRulersLength + 1;
		Segment1D dataSpace = new Segment1D(cL, cU - cL);
		List<ChartRulerSpace> resultRulers = new ArrayList<>();
		for ( RulerEntry e : includedEntries ) {
			rulerID = e.getRulerID();
			if ( rulerID.isLowerPosition() ) {
				cL -= e.getLength();
				resultRulers.add(new ChartRulerSpace(rulerID, new Segment1D(cL, e.getLength())));
			} else {
				resultRulers.add(new ChartRulerSpace(rulerID, new Segment1D(cU, e.getLength())));
				cU += e.getLength();
			}
		}
		return new ChartSpaceLayoutImpl(dataSpace, resultRulers);
	}
	
	private void checkExistence(ChartRulerID rulerID) {
		AxisDriver driver = drivers.get(rulerID.getAxisID());
		if ( driver == null ) {
			throw new IllegalArgumentException("Axis not exists: " + rulerID);
		}
		List<String> renderers = driver.getRendererIDs();
		if ( ! renderers.contains(rulerID.getRendererID()) ) {
			throw new IllegalArgumentException("Renderer not exists: " + rulerID);
		}
	}
	
	private List<RulerEntry> buildEntries() {
		ChartRulerID rulerID;
		RulerSetup setup = null;
		List<RulerEntry> entries = new ArrayList<>();
		List<String> axisIDs = new ArrayList<>(drivers.keySet());
		for ( int axisPrio = 0; axisPrio < axisIDs.size(); axisPrio ++ ) {
			String axisID = axisIDs.get(axisPrio);
			AxisDriver driver = drivers.get(axisID);
			List<String> rendererIDs = new ArrayList<>(driver.getRendererIDs());
			for ( int rendererPrio = 0; rendererPrio < rendererIDs.size(); rendererPrio ++ ) {
				String rendererID = rendererIDs.get(rendererPrio);
				rulerID = new ChartRulerID(axisID, rendererID, false);
				setup = rulerSetups.get(rulerID);
				if ( setup != null && setup.isVisible() ) {
					entries.add(new RulerEntry(rulerID,
											   setup.getPriority(),
											   axisPrio,
											   rendererPrio));
				}
				rulerID = new ChartRulerID(axisID, rendererID, true);
				setup = rulerSetups.get(rulerID);
				if ( setup != null && setup.isVisible() ) {
					entries.add(new RulerEntry(rulerID,
											   setup.getPriority(),
											   axisPrio,
											   rendererPrio));
				}
			}
		}
		Collections.sort(entries);
		return entries;
	}

}

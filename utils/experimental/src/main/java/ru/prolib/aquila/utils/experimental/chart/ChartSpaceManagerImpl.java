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
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.utils.experimental.chart.axis.AxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSpace;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;

public class ChartSpaceManagerImpl implements ChartSpaceManager {
	
	static class RulerEntry implements Comparable<RulerEntry> {
		private final RulerID rulerID;
		private final int rulerPriority;
		private final int axisPriority;
		private final int rendererPriority;
		private final int positionPriority;
		
		/**
		 * This attribute is to cache rulers length value to prevent
		 * calling of renderer method twice or more times.
		 */
		private int rulerLength;
		
		public RulerEntry(RulerID rulerID,
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
		
		public RulerID getRulerID() {
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
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("rulerID", rulerID)
					.append("rulerPrio", rulerPriority)
					.append("axisPrio", axisPriority)
					.append("rendererPrio", rendererPriority)
					.append("positionPrio", positionPriority)
					.append("rulerLength", rulerLength)
					.toString();
		}
		
	}
	
	static class GridLinesEntry implements Comparable<GridLinesEntry> {
		private final GridLinesSetup setup;
		private final int displayPriority;
		private final int axisPriority;
		private final int rendererPriority;
		
		public GridLinesEntry(GridLinesSetup setup,
							  int displayPriority,
							  int axisPriority,
							  int rendererPriority)
		{
			this.setup = setup;
			this.displayPriority = displayPriority;
			this.axisPriority = axisPriority;
			this.rendererPriority = rendererPriority;			
		}
		
		public GridLinesSetup getSetup() {
			return setup;
		}

		@Override
		public int compareTo(GridLinesEntry rhs) {
			return new CompareToBuilder()
					.append(displayPriority, rhs.displayPriority)
					.append(axisPriority, rhs.axisPriority)
					.append(rendererPriority, rhs.rendererPriority)
					.toComparison();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != GridLinesEntry.class ) {
				return false;
			}
			GridLinesEntry o = (GridLinesEntry) other;
			return new EqualsBuilder()
					.append(o.setup, setup)
					.append(o.displayPriority, displayPriority)
					.append(o.axisPriority, axisPriority)
					.append(o.rendererPriority, rendererPriority)
					.isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(112121219, 7752173)
					.append(setup)
					.append(displayPriority)
					.append(axisPriority)
					.append(rendererPriority)
					.toHashCode();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("setup", setup)
					.append("displayPrio", displayPriority)
					.append("axisPrio", axisPriority)
					.append("rendererPrio", rendererPriority)
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
	private final HashMap<RulerID, RulerSetup> rulerSetups;
	private final HashMap<RulerRendererID, GridLinesSetup> gridLinesSetups;
	
	ChartSpaceManagerImpl(LabelSizeStrategy labelSizeStrategy,
						  LinkedHashMap<String, AxisDriver> drivers,
						  HashMap<RulerID, RulerSetup> rulerSetups,
						  HashMap<RulerRendererID, GridLinesSetup> gridLinesSetups)
	{
		this.labelSizeStrategy = labelSizeStrategy;
		this.drivers = drivers;
		this.rulerSetups = rulerSetups;
		this.gridLinesSetups = gridLinesSetups;
	}
	
	public static ChartSpaceManager ofHorizontalSpace() {
		return new ChartSpaceManagerImpl(new HorizontalLabelSize(),
										 new LinkedHashMap<>(),
										 new HashMap<>(),
										 new HashMap<>());
	}
	
	public static ChartSpaceManager ofVerticalSpace() {
		return new ChartSpaceManagerImpl(new VerticalLabelSize(),
										 new LinkedHashMap<>(),
										 new HashMap<>(),
										 new HashMap<>());
	}

	@Override
	public void registerAxis(AxisDriver driver) {
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
	public void setRulerVisibility(RulerID rulerID, boolean visible) {
		getRulerSetup(rulerID).setVisible(visible);
	}

	@Override
	public void setRulerDisplayPriority(RulerID rulerID, int priority) {
		getRulerSetup(rulerID).setDisplayPriority(priority);
	}

	@Override
	public ChartSpaceLayout prepareLayout(Segment1D displaySpace,
													   Segment1D dataSpace,
													   Object device)
	{
		if ( ! displaySpace.contains(dataSpace) ) {
			throw new IllegalArgumentException("Data space is out of display space");
		}
		RulerID rulerID = null;
		int lowerRulersFreeLength = dataSpace.getStart() - displaySpace.getStart(),
			upperRulersFreeLength = displaySpace.getEnd() - dataSpace.getEnd();
		List<RulerEntry> includedRulers = new ArrayList<>();
		for ( RulerEntry e : buildRulerEntries() ) {
			rulerID = e.getRulerID();
			AxisDriver driver = drivers.get(rulerID.getAxisID());
			RulerRenderer renderer = driver.getRenderer(rulerID.getRendererID());
			int rulerLength = labelSizeStrategy.getLabelMaxSize(renderer, device);
			if ( rulerID.isLowerPosition() ) {
				if ( rulerLength > lowerRulersFreeLength ) {
					continue;
				}
				lowerRulersFreeLength -= rulerLength;
			} else {
				if ( rulerLength > upperRulersFreeLength ) {
					continue;
				}
				upperRulersFreeLength -= rulerLength;
			}
			e.setLength(rulerLength);
			includedRulers.add(e);
		}
		return toLayout(dataSpace.getStart(), dataSpace.getEnd() + 1, dataSpace, includedRulers, buildGLEntries());
	}

	@Override
	public ChartSpaceLayout prepareLayout(Segment1D displaySpace,
													   int rulersMaxSpace,
													   Object device)
	{
		if ( rulersMaxSpace > displaySpace.getLength() ) {
			throw new IllegalArgumentException("Space reserved for rulers is greater than display space");
		}
		RulerID rulerID = null;
		int lowerRulersLength = 0, upperRulersLength = 0;
		List<RulerEntry> includedRulers = new ArrayList<>();
		for ( RulerEntry e : buildRulerEntries() ) {
			rulerID = e.getRulerID();
			AxisDriver driver = drivers.get(rulerID.getAxisID());
			RulerRenderer renderer = driver.getRenderer(rulerID.getRendererID());
			int rulerLength = labelSizeStrategy.getLabelMaxSize(renderer, device);
			if ( lowerRulersLength + upperRulersLength + rulerLength > rulersMaxSpace ) {
				break;
			}
			e.setLength(rulerLength);
			includedRulers.add(e);
			if ( rulerID.isLowerPosition() ) {
				lowerRulersLength += rulerLength;
			} else {
				upperRulersLength += rulerLength;
			}
		}
		int cL = displaySpace.getStart() + lowerRulersLength;
		int cU = displaySpace.getEnd() - upperRulersLength + 1;
		return toLayout(cL, cU, new Segment1D(cL, cU - cL), includedRulers, buildGLEntries());
	}
	
	@Override
	public RulerSetup getRulerSetup(RulerID rulerID) {
		RulerSetup setup = rulerSetups.get(rulerID);
		if ( setup == null ) {
			setup = getRenderer(rulerID).createRulerSetup(rulerID);
			rulerSetups.put(rulerID, setup);
		}
		return setup;
	}

	@Override
	public RulerSetup getUpperRulerSetup(String axisID, String rendererID) {
		return getRulerSetup(new RulerID(axisID, rendererID, true));
	}

	@Override
	public RulerSetup getLowerRulerSetup(String axisID, String rendererID) {
		return getRulerSetup(new RulerID(axisID, rendererID, false));
	}

	@Override
	public GridLinesSetup getGridLinesSetup(RulerRendererID rendererID) {
		GridLinesSetup setup = gridLinesSetups.get(rendererID);
		if ( setup == null ) {
			setup = getRenderer(rendererID).createGridLinesSetup(rendererID);
			gridLinesSetups.put(rendererID, setup);
		}
		return setup;
	}

	@Override
	public GridLinesSetup getGridLinesSetup(String axisID, String rendererID) {
		return getGridLinesSetup(new RulerRendererID(axisID, rendererID));
	}
	
	private RulerRenderer getRenderer(RulerID rulerID) {
		return getRenderer(rulerID.getAxisID(), rulerID.getRendererID());
	}
	
	private RulerRenderer getRenderer(RulerRendererID rendererID) {
		return getRenderer(rendererID.getAxisID(), rendererID.getRendererID());
	}
	
	private RulerRenderer getRenderer(String axisID, String rendererID) {
		AxisDriver driver = drivers.get(axisID);
		if ( driver == null ) {
			throw new IllegalArgumentException("Axis not exists: " + axisID);
		}
		return driver.getRenderer(rendererID);
	}
	
	private List<RulerEntry> buildRulerEntries() {
		RulerID rulerID;
		RulerSetup setup = null;
		List<RulerEntry> entries = new ArrayList<>();
		List<String> axisIDs = new ArrayList<>(drivers.keySet());
		for ( int axisPrio = 0; axisPrio < axisIDs.size(); axisPrio ++ ) {
			String axisID = axisIDs.get(axisPrio);
			AxisDriver driver = drivers.get(axisID);
			List<String> rendererIDs = new ArrayList<>(driver.getRendererIDs());
			for ( int rendererPrio = 0; rendererPrio < rendererIDs.size(); rendererPrio ++ ) {
				String rendererID = rendererIDs.get(rendererPrio);
				rulerID = new RulerID(axisID, rendererID, false);
				setup = rulerSetups.get(rulerID);
				if ( setup != null && setup.isVisible() ) {
					entries.add(new RulerEntry(rulerID,
											   setup.getDisplayPriority(),
											   axisPrio,
											   rendererPrio));
				}
				rulerID = new RulerID(axisID, rendererID, true);
				setup = rulerSetups.get(rulerID);
				if ( setup != null && setup.isVisible() ) {
					entries.add(new RulerEntry(rulerID,
											   setup.getDisplayPriority(),
											   axisPrio,
											   rendererPrio));
				}
			}
		}
		Collections.sort(entries);
		return entries;
	}
	
	private List<GridLinesEntry> buildGLEntries() {
		List<GridLinesEntry> entries = new ArrayList<>();
		List<String> axisIDs = new ArrayList<>(drivers.keySet());
		for ( int axisPrio = 0; axisPrio < axisIDs.size(); axisPrio ++ ) {
			String axisID = axisIDs.get(axisPrio);
			AxisDriver driver = drivers.get(axisID);
			List<String> rendererIDs = new ArrayList<>(driver.getRendererIDs());
			for ( int rendererPrio = 0; rendererPrio < rendererIDs.size(); rendererPrio ++ ) {
				String rendererID = rendererIDs.get(rendererPrio);
				RulerRendererID rrid = new RulerRendererID(axisID, rendererID);
				GridLinesSetup setup = gridLinesSetups.get(rrid);
				if ( setup != null && setup.isVisible() ) {
					entries.add(new GridLinesEntry(setup, setup.getDisplayPriority(), axisPrio, rendererPrio));
				}
			}
		}
		Collections.sort(entries);
		return entries;
	}
	
	private ChartSpaceLayout toLayout(int cL,
									  int cU,
									  Segment1D dataSpace,
									  List<RulerEntry> includedRulers,
									  List<GridLinesEntry> includedGridLines)
	{
		List<RulerSpace> resultRulers = new ArrayList<>();
		for ( RulerEntry e : includedRulers ) {
			RulerID rulerID = e.getRulerID();
			if ( rulerID.isLowerPosition() ) {
				cL -= e.getLength();
				resultRulers.add(new RulerSpace(rulerID, new Segment1D(cL, e.getLength())));
			} else {
				resultRulers.add(new RulerSpace(rulerID, new Segment1D(cU, e.getLength())));
				cU += e.getLength();
			}
		}
		List<GridLinesSetup> resultGridLines = new ArrayList<>();
		for ( GridLinesEntry e : includedGridLines ) {
			resultGridLines.add(e.getSetup());
		}
		return new ChartSpaceLayoutImpl(dataSpace, resultRulers, resultGridLines);
	}

}

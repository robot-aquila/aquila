package ru.prolib.aquila.probe.scheduler.ui;

import java.awt.BorderLayout;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.probe.scheduler.SchedulerSlot;
import ru.prolib.aquila.probe.scheduler.SchedulerState;
import ru.prolib.aquila.probe.scheduler.SchedulerTask;

public class ScheduledTasksPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter timeFormat;
	
	static {
		timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	private final ZoneId zoneId;
	private final DefaultMutableTreeNode root;
	private final DefaultTreeModel treeModel;
	private final JTree tree;
	
	public ScheduledTasksPanel(IMessages messages, ZoneId zoneId) {
		setLayout(new BorderLayout());
		this.zoneId = zoneId;
		root = new DefaultMutableTreeNode(messages.get(ProbeMsg.STD_TREE_ROOT));
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void reloadTree(SchedulerState state) {
		root.removeAllChildren();
		List<Instant> list = new ArrayList<>(state.getTimeOfSlots());
		Collections.sort(list);
		for ( Instant time : list ) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(time.atZone(zoneId).format(timeFormat));
			root.add(node);
			SchedulerSlot slot = state.getSlot(time);
			for ( SchedulerTask task : slot.getTasks() ) {
				DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task.getRunnable()); 
				node.add(taskNode);
			}
		}
		tree.expandRow(0);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		treeModel.reload(root);
	}
	
}

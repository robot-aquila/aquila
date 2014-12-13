package ru.prolib.aquila.probe.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JToolBar;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.probe.internal.SimulationController;
import ru.prolib.aquila.ui.ClassLabels;

public class PROBEToolBar extends JToolBar
	implements ActionListener, EventListener
{
	private static final long serialVersionUID = -540449510842874693L;
	private final SimulationController ctrl;
	private final ClassLabels labels;
	private final JButton btnOptions, btnRunTo, btnStep,
		btnPauseRunRt, btnRunAll, btnFinish;
	private final Icon iconPause, iconRunRt;
	private final SelectTargetTimeDialogView selectTargetTimeDialog;
	
	public PROBEToolBar(SimulationController ctrl, ClassLabels texts) {
		super();
		this.ctrl = ctrl;
		this.labels = texts;
		selectTargetTimeDialog = new SelectTargetTimeDialog(texts);
		setName(texts.get("TOOLBAR_NAME"));
		btnOptions = makeButton("options.png", "options", "TTIP_OPTIONS");
		btnRunTo = makeButton("runto.png", "runto", "TTIP_RUNTO");
		btnStep = makeButton("step.png", "runto", "TTIP_STEP");
		btnPauseRunRt = makeButton("runrt.png", "runrt", "TTIP_RUNRT");
		btnRunAll = makeButton("runall.png", "runall", "TTIP_RUNALL");
		btnFinish = makeButton("finish.png", "finish", "TTIP_FINISH");
		iconPause = getIcon("pause.png");
		iconRunRt = btnPauseRunRt.getIcon();
		add(btnOptions);
		add(btnRunTo);
		add(btnStep);
		add(btnPauseRunRt);
		add(btnRunAll);
		add(btnFinish);
		ctrl.OnFinish().addListener(this);
		ctrl.OnPause().addListener(this);
		ctrl.OnRun().addListener(this);
	}
	
	private JButton makeButton(String iconFile, String actionId,
			String toolTipId)
	{
		JButton button = new JButton();
		button.setActionCommand(actionId);
		button.setToolTipText(labels.get(toolTipId));
		button.setIcon(getIcon(iconFile));
		button.addActionListener(this);
		return button;
	}
	
	private Icon getIcon(String fileSuffix) {
		return new ImageIcon("shared/images/probe_" + fileSuffix);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand(); 
		if ( cmd.equals("runrt") ) {
			btnPauseRunRt.setIcon(iconPause);
			btnPauseRunRt.setToolTipText(labels.get("TTIP_PAUSE"));
			btnPauseRunRt.setActionCommand("pause");
			// TODO: add realtime run controller
			ctrl.run();
		} else if ( cmd.equals("pause") ) {
			btnPauseRunRt.setIcon(iconRunRt);
			btnPauseRunRt.setToolTipText(labels.get("TTIP_RUNRT"));
			btnPauseRunRt.setActionCommand("runrt");
			ctrl.pause();
		} else if ( cmd.equals("runto") ) {
			DateTime startTime = ctrl.getRunInterval().getStart();
			DateTime selected = selectTargetTimeDialog.showDialog(startTime);
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(ctrl.OnFinish()) ) {
			ctrl.OnFinish().removeListener(this);
			ctrl.OnPause().removeListener(this);
			ctrl.OnRun().removeListener(this);
			disableAllButtons();
		} else if ( event.isType(ctrl.OnPause()) ) {
			btnRunTo.setEnabled(true);
			btnStep.setEnabled(true);
			btnPauseRunRt.setIcon(iconRunRt);
			
		} else if ( event.isType(ctrl.OnRun()) ) {
			
		}
	}
	
	private void disableAllButtons() {
		btnRunTo.setEnabled(false);
		btnStep.setEnabled(false);
		btnPauseRunRt.setEnabled(false);
		btnRunAll.setEnabled(false);
		btnFinish.setEnabled(false);
	}

}

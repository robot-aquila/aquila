package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalRegistry;

public class RobotBuilder {
	private final EditableTerminal terminal;
	private final MarketSignalRegistry msigRegistry;
	
	public RobotBuilder(EditableTerminal terminal, MarketSignalRegistry msigRegistry) {
		this.terminal = terminal;
		this.msigRegistry = msigRegistry;
	}
	
	public Robot buildBullDummy(RobotConfig config) {
		RobotData rData = new RobotData(terminal, config, msigRegistry.getSignal(config.getSignalID()));
		SMStateMachine automat = new SMBuilder()
				.addState(new SInit(rData), 		Const.S_INIT)
				.addState(new SBullWaitSig(rData), 	Const.S_WAIT_SIG)
				.addState(new SOpenLong(rData), 	Const.S_OPEN)
				.addState(new SCloseLong(rData), 	Const.S_CLOSE)
				.setInitialState(Const.S_INIT)
				
				.addTrans(Const.S_INIT, SInit.EOK, Const.S_WAIT_SIG)
				.addFinal(Const.S_INIT, SInit.EBR)
				.addFinal(Const.S_INIT, SInit.EER)
				
				.addTrans(Const.S_WAIT_SIG, SBullWaitSig.EOPN, Const.S_OPEN)
				.addTrans(Const.S_WAIT_SIG, SBullWaitSig.ECLS, Const.S_CLOSE) 
				.addFinal(Const.S_WAIT_SIG, SBullWaitSig.EBR)
				.addFinal(Const.S_WAIT_SIG, SBullWaitSig.EER)
				
				.addTrans(Const.S_OPEN, SOpenLong.EOK,  Const.S_WAIT_SIG)
				.addTrans(Const.S_OPEN, SOpenLong.ECLS, Const.S_CLOSE)
				.addFinal(Const.S_OPEN, SOpenLong.EBR)
				.addFinal(Const.S_OPEN, SOpenLong.EER)
				
				.addTrans(Const.S_CLOSE, SCloseLong.EOK,  Const.S_WAIT_SIG)
				.addTrans(Const.S_CLOSE, SCloseLong.EOPN, Const.S_OPEN)
				.addFinal(Const.S_CLOSE, SCloseLong.EBR)
				.addFinal(Const.S_CLOSE, SCloseLong.EER)
				.build();
		return new Robot(automat, rData);
	}

}

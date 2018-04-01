package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.aquila.utils.experimental.sst.msig.BreakSignal;

public class RobotBuilder {
	private final DataServiceLocator serviceLocator;
	private final BreakSignal breakSignal;
	
	public RobotBuilder(DataServiceLocator serviceLocator, BreakSignal breakSignal) {
		this.serviceLocator = serviceLocator;
		this.breakSignal = breakSignal;
	}
	
	public Robot buildBullDummy(RobotConfig config) {
		RobotData rData = new RobotData(serviceLocator.getTerminal(), config, breakSignal);
		SMStateMachine automat = new SMBuilder()
				.addState(new SInit(rData), 								Const.S_INIT)
				.addState(new SHInitMarketSignal(rData, serviceLocator),	Const.S_INIT_MSIGNAL)
				.addState(new SBullWaitSig(rData), 							Const.S_WAIT_SIG)
				.addState(new SOpenLong(rData), 							Const.S_OPEN)
				.addState(new SCloseLong(rData), 							Const.S_CLOSE)
				.addState(new SHDropMarketSignal(rData, serviceLocator),	Const.S_DROP_MSIGNAL)
				.setInitialState(Const.S_INIT)
				
				.addTrans(Const.S_INIT, SInit.EOK, Const.S_INIT_MSIGNAL)
				.addFinal(Const.S_INIT, SInit.EBR)
				.addFinal(Const.S_INIT, SInit.EER)
				
				.addTrans(Const.S_INIT_MSIGNAL, SHInitMarketSignal.EOK, Const.S_WAIT_SIG)
				.addFinal(Const.S_INIT_MSIGNAL, SHInitMarketSignal.EBR)
				.addFinal(Const.S_INIT_MSIGNAL, SHInitMarketSignal.EER)
				
				.addTrans(Const.S_WAIT_SIG, SBullWaitSig.EOPN,	Const.S_OPEN)
				.addTrans(Const.S_WAIT_SIG, SBullWaitSig.ECLS,	Const.S_CLOSE) 
				.addTrans(Const.S_WAIT_SIG, SBullWaitSig.EBR,	Const.S_DROP_MSIGNAL)
				.addFinal(Const.S_WAIT_SIG, SBullWaitSig.EER)
				
				.addTrans(Const.S_OPEN, SOpenLong.EOK,  	Const.S_WAIT_SIG)
				.addTrans(Const.S_OPEN, SOpenLong.ECLS, 	Const.S_CLOSE)
				.addTrans(Const.S_OPEN, SOpenLong.EBR,		Const.S_DROP_MSIGNAL)
				.addFinal(Const.S_OPEN, SOpenLong.EER)
				
				.addTrans(Const.S_CLOSE, SCloseLong.EOK,	Const.S_WAIT_SIG)
				.addTrans(Const.S_CLOSE, SCloseLong.EOPN,	Const.S_OPEN)
				.addTrans(Const.S_CLOSE, SCloseLong.EBR,	Const.S_DROP_MSIGNAL)
				.addFinal(Const.S_CLOSE, SCloseLong.EER)
				
				.addFinal(Const.S_DROP_MSIGNAL, SHDropMarketSignal.EOK)
				.addFinal(Const.S_DROP_MSIGNAL, SHDropMarketSignal.EBR)
				.addFinal(Const.S_DROP_MSIGNAL, SHDropMarketSignal.EER)
				
				.build();
		return new Robot(automat, rData);
	}

}

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
		RobotState rData = new RobotState(serviceLocator.getTerminal(), config, breakSignal);
		SMStateMachine automat = new SMBuilder()
				.addState(new SHInit(rData), 								Const.S_INIT)
				.addState(new SHInitMarketSignal(rData, serviceLocator),	Const.S_INIT_MSIGNAL)
				.addState(new SHBullWaitSig(rData), 							Const.S_WAIT_SIG)
				.addState(new SHOpenLong(rData), 							Const.S_OPEN)
				.addState(new SHCloseLong(rData), 							Const.S_CLOSE)
				.addState(new SHDropMarketSignal(rData, serviceLocator),	Const.S_DROP_MSIGNAL)
				.setInitialState(Const.S_INIT)
				
				.addTrans(Const.S_INIT, SHInit.EOK, Const.S_INIT_MSIGNAL)
				.addFinal(Const.S_INIT, SHInit.EBR)
				.addFinal(Const.S_INIT, SHInit.EER)
				
				.addTrans(Const.S_INIT_MSIGNAL, SHInitMarketSignal.EOK, Const.S_WAIT_SIG)
				.addFinal(Const.S_INIT_MSIGNAL, SHInitMarketSignal.EBR)
				.addFinal(Const.S_INIT_MSIGNAL, SHInitMarketSignal.EER)
				
				.addTrans(Const.S_WAIT_SIG, SHBullWaitSig.EOPN,	Const.S_OPEN)
				.addTrans(Const.S_WAIT_SIG, SHBullWaitSig.ECLS,	Const.S_CLOSE) 
				.addTrans(Const.S_WAIT_SIG, SHBullWaitSig.EBR,	Const.S_DROP_MSIGNAL)
				.addFinal(Const.S_WAIT_SIG, SHBullWaitSig.EER)
				
				.addTrans(Const.S_OPEN, SHOpenLong.EOK,  	Const.S_WAIT_SIG)
				.addTrans(Const.S_OPEN, SHOpenLong.ECLS, 	Const.S_CLOSE)
				.addTrans(Const.S_OPEN, SHOpenLong.EBR,		Const.S_DROP_MSIGNAL)
				.addFinal(Const.S_OPEN, SHOpenLong.EER)
				
				.addTrans(Const.S_CLOSE, SHCloseLong.EOK,	Const.S_WAIT_SIG)
				.addTrans(Const.S_CLOSE, SHCloseLong.EOPN,	Const.S_OPEN)
				.addTrans(Const.S_CLOSE, SHCloseLong.EBR,	Const.S_DROP_MSIGNAL)
				.addFinal(Const.S_CLOSE, SHCloseLong.EER)
				
				.addFinal(Const.S_DROP_MSIGNAL, SHDropMarketSignal.EOK)
				.addFinal(Const.S_DROP_MSIGNAL, SHDropMarketSignal.EBR)
				.addFinal(Const.S_DROP_MSIGNAL, SHDropMarketSignal.EER)
				
				.build();
		return new Robot(automat, rData);
	}

}

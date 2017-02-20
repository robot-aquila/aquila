package ru.prolib.aquila.utils.experimental.sst;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceImpl;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceSATImpl;
import ru.prolib.aquila.probe.datasim.SymbolUpdateSourceImpl;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerControlToolbar;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerTaskFilter;
import ru.prolib.aquila.probe.scheduler.ui.SymbolUpdateTaskFilter;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.PortfolioListTableModel;
import ru.prolib.aquila.ui.form.PositionListTableModel;
import ru.prolib.aquila.ui.form.SecurityListTableModel;
import ru.prolib.aquila.utils.experimental.CmdLine;
import ru.prolib.aquila.utils.experimental.Experiment;
import ru.prolib.aquila.web.utils.finam.datasim.FinamL1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;
import ru.prolib.aquila.web.utils.moex.MoexSymbolUpdateReaderFactory;

public class SecuritySimulationTest implements Experiment {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SecuritySimulationTest.class);
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public int run(Scheduler scheduler, CommandLine cmd, CountDownLatch exitSignal) {
		final IMessages messages = new Messages();
		final File root = new File(cmd.getOptionValue(CmdLine.LOPT_ROOT));
		
		// Initialize terminal
		EditableTerminal terminal = new BasicTerminalBuilder()
			.withTerminalID("SECURITY_SIMULATION")
			.withScheduler(scheduler)
			.withDataProvider(newDataProvider(scheduler, root))
			.buildTerminal();
		terminal.getEditablePortfolio(new Account("TEST-ACCOUNT"))
			.consume(new DeltaUpdateBuilder()
				.withToken(PortfolioField.BALANCE, 10000.0d)
				.buildUpdate());
		Set<Symbol> symbols = null;
		try {
			symbols = new MoexContractFileStorage(root).getSymbols();
		} catch ( DataStorageException e ) {
			logger.error("Error reading symbol list: ", e);
			return 1;
		}
		for ( Symbol symbol : symbols ) {
			terminal.subscribe(symbol);
		}
		
		// Initialize the main frame
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				exitSignal.countDown();
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800,  600);
		frame.setTitle("Security simulation test");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        mainPanel.add(topPanel, BorderLayout.PAGE_START);
		if ( scheduler instanceof ru.prolib.aquila.probe.SchedulerImpl ) {
			List<SchedulerTaskFilter> filters = new ArrayList<>();
			filters.add(new SymbolUpdateTaskFilter(messages));
			topPanel.add(new SchedulerControlToolbar(messages, (SchedulerImpl) scheduler, filters));
		}
		
		JTabbedPane tabPanel = new JTabbedPane();
        mainPanel.add(tabPanel, BorderLayout.CENTER);
        
        SecurityListTableModel securityTableModel = new SecurityListTableModel(messages);
        securityTableModel.add(terminal);
        JTable table = new JTable(securityTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<SecurityListTableModel>(securityTableModel));
        tabPanel.add("Securities", new JScrollPane(table));
        new TableModelController(securityTableModel, frame);
        
        PortfolioListTableModel portfolioTableModel = new PortfolioListTableModel(messages);
        portfolioTableModel.add(terminal);
        table = new JTable(portfolioTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<PortfolioListTableModel>(portfolioTableModel));
        tabPanel.add("Accounts", new JScrollPane(table));
        new TableModelController(portfolioTableModel, frame);
        
        PositionListTableModel positionTableModel = new PositionListTableModel(messages);
        positionTableModel.add(terminal);
        table = new JTable(positionTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<PositionListTableModel>(positionTableModel));
        tabPanel.add("Positions", new JScrollPane(table));
        new TableModelController(positionTableModel, frame);
        
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
        
		return 0;
	}

	@Override
	public int getExitCode() {
		return 0;
	}
	
	private DataProvider newDataProvider(Scheduler scheduler, File root) {
		return new DataProviderImpl(
			new SymbolUpdateSourceImpl(scheduler, new MoexSymbolUpdateReaderFactory(root)),
			new L1UpdateSourceSATImpl(new L1UpdateSourceImpl(scheduler, new FinamL1UpdateReaderFactory(root))));
	}

}

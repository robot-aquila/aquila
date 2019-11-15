package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;

/**
 * Common table model to use in combination with {@link OSCRepository} as data source.
 * Just define list of tokens and map those tokens to headers.
 * Do inject everything needed into constructor and use without additional class inheritance.
 * <p>
 * @param <EntityType> - entity class
 */
public class OSCRepositoryTableModel<EntityType extends ObservableStateContainer>
	extends AbstractTableModel
	implements EventListener, ITableModel
{
	private static final long serialVersionUID = 1L;
	
	private final IMessages messages;
	private final OSCRepository<?, EntityType> repository;
	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private final List<EntityType> entities;
	private boolean subscribed = false;
	
	public OSCRepositoryTableModel(
			IMessages messages,
			OSCRepository<?, EntityType> repository,
			List<Integer> column_id_list,
			Map<Integer, MsgID> column_id_to_header,
			List<EntityType> entities
		)
	{
		this.messages = messages;
		this.repository = repository;
		this.columnIndexToColumnID = column_id_list;
		this.columnIDToColumnHeader = column_id_to_header;
		this.entities = entities;
	}
	
	public OSCRepositoryTableModel(
			IMessages messages,
			OSCRepository<?, EntityType> repository,
			List<Integer> column_id_list,
			Map<Integer, MsgID> column_id_to_header
		)
	{
		this(messages, repository, column_id_list, column_id_to_header, new ArrayList<>());
	}

	@Override
	public int getColumnCount() {
		return columnIndexToColumnID.size();
	}

	@Override
	public int getRowCount() {
		return entities.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if ( row >= entities.size() ) {
			return null;
		}
		return getColumnValue(entities.get(row), getColumnID(col));
	}
	
	protected Object getColumnValue(EntityType entity, int columnID) {
		return entity.getObject(columnID);
	}

	@Override
	public int getColumnIndex(int columnID) {
		return columnIndexToColumnID.indexOf(columnID);
	}

	@Override
	public int getColumnID(int columnIndex) {
		return columnIndexToColumnID.get(columnIndex);
	}
	
	@Override
	public String getColumnName(int col) {
		MsgID id = columnIDToColumnHeader.get(columnIndexToColumnID.get(col));
		if ( id == null ) {
			return "NULL_ID#" + col; 
		}
		return messages.get(id);
	}
	
	@Override
	public void startListeningUpdates() {
		if ( subscribed ) {
			return;
		}
		cacheDataAndSubscribeEvents();
		subscribed = true;
	}

	@Override
	public void stopListeningUpdates() {
		if ( ! subscribed ) {
			return;
		}
		unsubscribe();
		clear();
		fireTableDataChanged();
		subscribed = false;
	}

	@Override
	public void onEvent(Event event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				processEvent((OSCEventImpl) event);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected EntityType toEntity(OSCEventImpl event) {
		return (EntityType) event.getContainer();
	}
	
	protected void processEvent(OSCEventImpl event) {
		if ( ! subscribed ) {
			return;
		}
		if ( event.isType(repository.onEntityUpdate()) ) {
			EntityType entity = toEntity(event);
			Integer row = getIndexOfEntity(entity);
			if ( row == null ) {
				row = addEntity(entity);
				if ( row != null ) {
					fireTableRowsInserted(row, row);
				}
			} else {
				fireTableRowsUpdated(row, row);
			}
		} else if ( event.isType(repository.onEntityClose()) ) {
			EntityType entity = toEntity(event);
			Integer row = removeEntity(entity);
			if ( row != null ) {
				fireTableRowsDeleted(row, row);
			}
		}
	}
	
	/**
	 * Get entity by its position.
	 * <p>
	 * @param rowIndex - row index
	 * @return entity instance or null if no entity exists
	 */
	public EntityType getEntity(int rowIndex) {
		return entities.get(rowIndex);
	}

	protected void clear() {
		entities.clear();
	}

	@Override
	public void close() {
		stopListeningUpdates();
	}
	
	protected void cacheDataAndSubscribeEvents() {
		subscribe();
		int count_added = 0, first_row = entities.size();
		for ( EntityType entity : repository.getEntities() ) {
			if ( addEntity(entity) != null ) {
				count_added ++;
			}
		}
		if ( count_added > 0 ) {
			fireTableRowsInserted(first_row, first_row + count_added - 1);
		}
	}

	protected void subscribe() {
		repository.onEntityUpdate().addListener(this);
		repository.onEntityClose().addListener(this);
	}
	
	protected void unsubscribe() {
		repository.onEntityUpdate().removeListener(this);
		repository.onEntityClose().removeListener(this);
	}
	
	protected Integer getIndexOfEntity(EntityType entity) {
		int index = entities.indexOf(entity);
		return index == -1 ? null : index;
	}
	
	/**
	 * Add entity to local cache.
	 * <p>
	 * @param entity - the entity instance to add
	 * @return index of entity in local cache or null if entity was not added
	 * (in case if it is already in cache)
	 */
	private Integer addEntity(EntityType entity) {
		if ( ! entities.contains(entity) ) {
			entities.add(entity);
			return entities.size() - 1;
		} else {
			return null;
		}
	}
	
	/**
	 * Remove entity from local cache.
	 * <p>
	 * @param entity - the entity instance to remove
	 * @return index of removed entity of null if entity was not found
	 */
	private Integer removeEntity(EntityType entity) {
		Integer row = getIndexOfEntity(entity);
		if ( row != null ) {
			entities.remove((int) row);
			return row;
		} else {
			return null;
		}
	}

}

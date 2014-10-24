/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.web.layouteditor.client.ui.page;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.web.gwt.client.ui.page.SelectionManager;
import org.primaresearch.web.gwt.client.ui.page.SelectionManager.SelectionListener;
import org.primaresearch.web.gwt.shared.page.ContentObjectC;
import org.primaresearch.web.layouteditor.client.AppConstants;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * A combination of region types and sub-types as one list.
 * 
 * @author Christian Clausner
 *
 */
public class SimpleRegionTypeEditor implements SelectionListener {

	/** Access to externalised strings */
	private static final AppConstants CONSTANTS = GWT.create(AppConstants.class);

	private static final int STATE_ENABLED = 1;
	private static final int STATE_READONLY = 2;
	private static final int STATE_DISABLED = 3;
	private int state; //ENABLED, READONLY, or DISABLED
	
	private CellList<RegionTypeInfo> cellList;
	private ListDataProvider<RegionTypeInfo> dataProvider;
	private SingleSelectionModel<RegionTypeInfo> selectionModel;
	private SelectionModel<RegionTypeInfo> noSelectionModel;
	private Set<RegionTypeSelectionListener> listeners = new HashSet<RegionTypeSelectionListener>();
	private boolean listenersSuspended = false;
	private RegionTypeCell cellTemplate;
	
	public Widget getWidget() {
		return cellList;
	}
	
	/**
	 * Constructor
	 * @param selectionManager Page content object selection manager
	 */
	public SimpleRegionTypeEditor(final SelectionManager selectionManager) {
		state = STATE_ENABLED;
		cellTemplate = new RegionTypeCell(state != STATE_ENABLED);
		
		cellList = new CellList<RegionTypeInfo>(cellTemplate);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		
		//Add a selection model so we can select cells.
	    selectionModel = new SingleSelectionModel<RegionTypeInfo>();
	    noSelectionModel = new NoSelectionModel<RegionTypeInfo>();
	    //if (enabled) //enable
	    	cellList.setSelectionModel(selectionModel, DefaultSelectionEventManager.<RegionTypeInfo>createDefaultManager());
	    //else //disable
	    //	cellList.setSelectionModel(noSelectionModel, DefaultSelectionEventManager.<RegionTypeInfo>createWhitelistManager());
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	    	public void onSelectionChange(SelectionChangeEvent event) {
	    		if (listenersSuspended || state == STATE_READONLY)
	    			return;
	    		
    			RegionTypeInfo newType = selectionModel.getSelectedObject();
    			
    			if (newType == null)
    				return;
    			
    			RegionType selectedType = newType.type;
    			String selectedsubType = newType.subType;
    			
    			//Look if the region has a type from the list of available types
				ContentObjectC selObj = null;
    			if (selectionManager.getSelection().size() == 1 
    					&& (selectionManager.getSelection().iterator().next().getType() instanceof RegionType)) {
    				selObj = selectionManager.getSelection().iterator().next();
    			}
    			RegionTypeInfo oldTypeInfo = findRegionTypeInfo(selObj);
    			
    			if (selObj != null && oldTypeInfo != null && oldTypeInfo.equals(newType)) //No change
    				return;
    			
    			//If the region has a type from the list and 'Other' has been selected,
    			//change to 'Unknown'
    			if (oldTypeInfo != null && selectedType == null) {
    				selectedType = RegionType.UnknownRegion;
    				selectedsubType = "";
    			}
    			
    			notifyListenersRegionTypeSelected(selectedType, selectedsubType);
	    	}
	    });
	    
	    //Add cells
	    dataProvider = new ListDataProvider<RegionTypeInfo>();
	    dataProvider.addDataDisplay(cellList);
	    List<RegionTypeInfo> types = dataProvider.getList();
	    
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelPageHeader(), CONSTANTS.RegionLabelDescriptionPageHeader(), 	
				RegionType.TextRegion, 		"header"));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelPageNumber(), CONSTANTS.RegionLabelDescriptionPageNumber(), 	
				RegionType.TextRegion, 		"page-number", true));

	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelTitle(), CONSTANTS.RegionLabelDescriptionTitle(), 		
				RegionType.TextRegion, 		"heading"));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelParagraph(), CONSTANTS.RegionLabelDescriptionParagraph(), 		
				RegionType.TextRegion, 		"paragraph", true));

	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelParatext(), CONSTANTS.RegionLabelDescriptionParatext(), 	
				RegionType.TextRegion, 		"marginalia"));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelPrintersMark(), CONSTANTS.RegionLabelDescriptionPrintersMark(), 	
				RegionType.TextRegion, 		"signature-mark"));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelOtherText(), CONSTANTS.RegionLabelDescriptionOtherText(), 	
				RegionType.TextRegion, 		"other", true));

	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelImage(), CONSTANTS.RegionLabelDescriptionImage(), 			
				RegionType.ImageRegion, 		""));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelDecoration(), CONSTANTS.RegionLabelDescriptionDecoration(), 	
				RegionType.GraphicRegion, 	"decoration"));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelSeparator(), CONSTANTS.RegionLabelDescriptionSeparator(), 		
				RegionType.SeparatorRegion, 	""));
	    types.add(new RegionTypeInfo(	CONSTANTS.RegionLabelOther(), CONSTANTS.RegionLabelDescriptionOther(), 		
				null, 	""));
	    
	    dataProvider.refresh();
	    

	}
	
	/**
	 * Sets the state of the editor (ENABLED, READONLY, or DISABLED)
	 */
	public void setState(int state) {
		setState(state, null);
	}
	
	/**
	 * Sets the state of the editor 
	 * @param state ENABLED, READONLY, or DISABLED
	 * @param selectedType Currently selected region label
	 */
	public void setState(int state, RegionTypeInfo selectedType) {
		
		if (this.state == state) //No change
			return;
		
		this.state = state;
		cellTemplate.setReadOnly(state != STATE_ENABLED);
		cellList.redraw();
		if (state == STATE_ENABLED)
		    cellList.setSelectionModel(selectionModel, DefaultSelectionEventManager.<RegionTypeInfo>createDefaultManager());
		else if (state == STATE_READONLY)
			cellList.setSelectionModel(new ReadOnlySelectionModel(selectedType, null), DefaultSelectionEventManager.<RegionTypeInfo>createWhitelistManager());
		else //DISABLED
			cellList.setSelectionModel(noSelectionModel, DefaultSelectionEventManager.<RegionTypeInfo>createWhitelistManager());
	}
	

	@Override
	public void selectionChanged(SelectionManager manager) {
		selectionModel.clear();
		
		ContentObjectC selectedObject = null;
		
		if (manager.getSelection().size() >= 1)
			selectedObject = manager.getSelection().iterator().next();
		
		if (manager.getSelection().size() == 1 && (selectedObject.getType() instanceof RegionType)) {
			
			RegionTypeInfo selInfo = findRegionTypeInfo(selectedObject);
			setState(selectedObject.isReadOnly() ? STATE_READONLY : STATE_ENABLED, selInfo);

			if (selInfo != null) {
				listenersSuspended = true;
				selectionModel.setSelected(selInfo, true);
				listenersSuspended = false;
			}
		} else { //No valid selection
			setState(manager.getSelection().isEmpty() ? STATE_ENABLED : STATE_DISABLED);
		}
	}
	
	/**
	 * Looks for a specific combination of type/subtype in the list of available ones.
	 * @param region Source of type and sub-type to look for
	 * @return The type info object if found or null
	 */
	private RegionTypeInfo findRegionTypeInfo(ContentObjectC region) {
		if (region == null)
			return null;
		ContentType regionType = region.getType();
		String subType = "";
		VariableMap attrs = region.getAttributes();
		if (attrs != null) {
			Variable attr = attrs.get("type");
			if (attr != null && attr.getValue() != null)
				subType = attr.getValue().toString();
		}

		List<RegionTypeInfo> types = dataProvider.getList(); 
	    for (Iterator<RegionTypeInfo> it=types.iterator(); it.hasNext(); ) {
	    	RegionTypeInfo typeInfo = it.next();
	    	if (typeInfo.type == null || (typeInfo.type.equals(regionType) && typeInfo.subType.equals(subType))) {
	    		return typeInfo;
	    	}
	    }
	    return null;
	}
	
	public void addRegionTypeSelectionListener(RegionTypeSelectionListener listener) {
		listeners.add(listener);
	}
	
	public void removeRegionTypeSelectionListener(RegionTypeSelectionListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListenersRegionTypeSelected(RegionType selectedType, String selectedSubType) {
		for (Iterator<RegionTypeSelectionListener> it=listeners.iterator(); it.hasNext(); ) {
			it.next().regionTypeSelected(selectedType, selectedSubType);
		}
	}

	
	/**
	 * Region type and sub-type (if applicable).
	 * 
	 * @author Christian Clausner
	 *
	 */
	private static class RegionTypeInfo {
		public String caption;
		public String description;
		public RegionType type;
		public String subType;
		public boolean lastInTypeGroup;
		
		public RegionTypeInfo(String caption, String description, RegionType type, String subType) {
			this(caption, description, type, subType, false);
		}

		public RegionTypeInfo(String caption, String description, RegionType type, String subType, boolean lastInTypeGroup) {
			this.caption = caption;
			this.description = description;
			this.type = type;
			this.subType = subType;
			this.lastInTypeGroup = lastInTypeGroup;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof RegionTypeInfo))
				return false;
			RegionTypeInfo info2 = (RegionTypeInfo)other;
			
			if (this.type == null && info2.type != null)
				return false;

			if (this.type == null && info2.type == null)
				return true;

			if (this.subType == null && info2.subType != null
				|| this.subType != null && info2.subType == null)
				return false;

			if (this.subType == null && info2.subType == null)
				return true;

			return this.type.equals(info2.type) && this.subType.equals(info2.subType);
		}
	}
	
	/**
	 * The Cell used to render a {@link RegionTypeInfo}.
	 */
	private static class RegionTypeCell extends AbstractCell<RegionTypeInfo> {

		private boolean readOnly;
		
	    public RegionTypeCell(boolean readOnly) {
	    	this.readOnly = readOnly;
	    }
	    
	    public void setReadOnly(boolean readOnly) {
	    	this.readOnly = readOnly;
	    }

	    @Override
	    public void render(Context context, RegionTypeInfo value, SafeHtmlBuilder sb) {
	    	// Value can be null, so do a null check..
	    	if (value == null) {
	    		return;
	    	}

	    	String classes = null;
	    	if (readOnly)
	    		classes = "simpleRegionTypeEditorCell-disabled";
	    	else
	    		classes = "simpleRegionTypeEditorCell";
	    	
	    	if (value.lastInTypeGroup)
	    		classes += " regionLabelSeparator";

    		sb.appendHtmlConstant("<table class=\""+classes+"\" title=\""+value.description+"\">");

	    	// Add the contact image.
	    	sb.appendHtmlConstant("<tr><td>");
	    	sb.appendHtmlConstant(value.caption);
	    	sb.appendHtmlConstant("</td></tr>");

	    	sb.appendHtmlConstant("</table>");
	    }
	    
        @Override
        public void onBrowserEvent(Context context, Element parent, RegionTypeInfo value, NativeEvent event, ValueUpdater<RegionTypeInfo> valueUpdater) {
            if(readOnly)
                event.preventDefault(); //Consume event
            else
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }
	}
	
	/**
	 * Listener for user selection of new region type.
	 * @author Christian Clausner
	 *
	 */
	public static interface RegionTypeSelectionListener {
		/** Called when another region type (label) has been selected */
		public void regionTypeSelected(RegionType selectedType, String selectedSubType);
	}
	
	
	/**
	 * Selection model for READONLY state (fixed selection).
	 * @author Christian Clausner
	 *
	 */
	private static class ReadOnlySelectionModel extends SelectionModel.AbstractSelectionModel<RegionTypeInfo> {

		private RegionTypeInfo selectedObject;
		
		protected ReadOnlySelectionModel(RegionTypeInfo selectedObject, ProvidesKey<RegionTypeInfo> keyProvider) {
			super(keyProvider);
			this.selectedObject = selectedObject;
		}

		@Override
		public boolean isSelected(RegionTypeInfo object) {
			return selectedObject == object;
		}

		@Override
		public void setSelected(RegionTypeInfo object, boolean selected) {
		}
		
	}

}

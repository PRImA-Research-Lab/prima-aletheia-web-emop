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
package org.primaresearch.web.layouteditor.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.maths.geometry.Rect;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.GwtDecimalFormatter;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.web.gwt.client.log.LogManager;
import org.primaresearch.web.gwt.client.page.PageLayoutC;
import org.primaresearch.web.gwt.client.page.PageSyncManager;
import org.primaresearch.web.gwt.client.page.PageSyncManager.PageSyncListener;
import org.primaresearch.web.gwt.client.ui.DocumentImageListener;
import org.primaresearch.web.gwt.client.ui.DocumentImageLoader;
import org.primaresearch.web.gwt.client.ui.keyboard.VirtualKeyboard;
import org.primaresearch.web.gwt.client.ui.page.PageScrollView;
import org.primaresearch.web.gwt.client.ui.page.PageScrollView.ZoomChangeListener;
import org.primaresearch.web.gwt.client.ui.page.SelectionManager;
import org.primaresearch.web.gwt.client.ui.page.SelectionManager.SelectionListener;
import org.primaresearch.web.gwt.client.ui.page.TextContentView;
import org.primaresearch.web.gwt.client.ui.page.TextContentView.TextContentViewChangeListener;
import org.primaresearch.web.gwt.client.ui.page.renderer.ContentSelectionRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.renderer.PageContentRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.renderer.SelectionFocusRendererPlugin;
import org.primaresearch.web.gwt.client.ui.page.tool.CreatePageObjectTool;
import org.primaresearch.web.gwt.client.ui.page.tool.CreatePageObjectTool.DrawingTool;
import org.primaresearch.web.gwt.client.ui.page.tool.DeleteContentObjectTool;
import org.primaresearch.web.gwt.client.ui.page.tool.MoveRegionTool;
import org.primaresearch.web.gwt.client.ui.page.tool.ResizeRegionTool;
import org.primaresearch.web.gwt.client.ui.page.tool.controls.ContentObjectToolbar;
import org.primaresearch.web.gwt.client.ui.page.tool.controls.ContentObjectToolbarButton;
import org.primaresearch.web.gwt.client.ui.page.tool.drawing.EditOutlineTool;
import org.primaresearch.web.gwt.client.ui.page.tool.drawing.PageViewTool;
import org.primaresearch.web.gwt.client.ui.page.tool.drawing.PageViewToolListener;
import org.primaresearch.web.gwt.client.user.UserManager;
import org.primaresearch.web.gwt.client.user.UserManager.LogOnListener;
import org.primaresearch.web.gwt.client.variable.VariableMapSyncService;
import org.primaresearch.web.gwt.client.variable.VariableMapSyncServiceAsync;
import org.primaresearch.web.gwt.shared.page.ContentObjectC;
import org.primaresearch.web.gwt.shared.page.ContentObjectSync;
import org.primaresearch.web.layouteditor.client.ui.page.SimpleRegionTypeEditor;
import org.primaresearch.web.layouteditor.client.ui.page.SimpleRegionTypeEditor.RegionTypeSelectionListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point class for WebLayoutEditor.<br>
 * <br>
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Christian Clausner
 */
public class WebLayoutEditor implements EntryPoint, ResizeHandler, DocumentImageListener, ZoomChangeListener,
									KeyPressHandler, KeyDownHandler, LogOnListener,
									PageSyncListener, TextContentViewChangeListener,
									SelectionListener, RegionTypeSelectionListener{
	/** Access to externalised strings */
	private static final AppConstants CONSTANTS = GWT.create(AppConstants.class);

	private int resizeRequiredExtraHeight; //Height for header and margin (needed for resize handling)
	
	private static final boolean ENABLE_CONTENT_TYPE_SELECTOR = false; //Switch for showing/hiding the selection control for regions/lines
	
	private static final int ERROR_ON_MODULE_LOAD 					= 1;
	private static final int ERROR_CREATING_TOOLBAR 				= 2;
	private static final int ERROR_CREATING_TEXT_DIALOG				= 3;
	private static final int ERROR_LOADING_DOCUMENT_DATA 			= 4;
	private static final int ERROR_ON_RESIZE						= 5;
	private static final int ERROR_UNSPECIFIED						= 6;
	private static final int ERROR_CREATING_LAYOUT_OBJECT_TOOLS 	= 7;
	private static final int ERROR_SHOWING_HELP_DIALOG				= 8;
	private static final int ERROR_REVERT_CONFIRMATION_DIALOG 		= 9;
	private static final int ERROR_REGION_TYPE_SELECTION_CHANGE 	= 10;
	private static final int ERROR_REGION_TYPE_SYNCHRONIZED			= 11;
	private static final int ERROR_ON_SELECTION_CHANGE				= 12;
	
	private LogManager logManager = new LogManager("WebLayoutEditor");
	private UserManager userManager = new UserManager();
	private SelectionManager selectionManager = new SelectionManager();
	private ContentObjectC selectedContentObject = null;
	private DocumentImageLoader imageLoader;
	private VariableMapSyncServiceAsync keyboardSyncService = GWT.create(VariableMapSyncService.class);
	private PageSyncManager pageSync;
	
	private PageLayoutC pageLayout;

	private PageScrollView pageView;
	private Label regionTypeEditorHeading;
	private SimpleRegionTypeEditor regionTypeEditor; 
	private TextContentView textContentView;
	private String currentPageObjectType = "Region"; 
	private SplitLayoutPanel splitPanel;
	private PageContentRendererPlugin pageContentRendererPlugin;
	private ContentSelectionRendererPlugin selectionRendererPlugin;
	private Widget toolbar;
	private PushButton zoomIn;
	private PushButton zoomOut;
	private ToggleButton showRegions;
	private ToggleButton showTextLines;
	private PushButton nextObject;
	private PushButton previousObject;
	private FocusPanel focusPanel;
	private VerticalPanel mainPanel;
	private DialogBox textDialog;
	private VirtualKeyboard virtualKeyboard;
	private PushButton revertButton;
	private Label documentTitle;
	private ContentObjectToolbarButton deleteObjectToolbarButton;
	private ContentObjectToolbarButton editOutlineToolbarButton;
	private ContentObjectToolbarButton editTextToolbarButton;
	private DialogBox errorMessageBox;
	private Label errorMessage;
	private Timer errorMessageTimer;

	/** Mark all content objects that will be loaded from the server next as 'read-only' */
	private boolean makePageContentObjectsReadOnly = true;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		try {
			//Set the number formatter (had to be decoupled since GWT doesn't work with DecimalFormat)
			DoubleValue.setFormatter(new GwtDecimalFormatter(DoubleValue.defaultFormatPattern));
			
			resizeRequiredExtraHeight = Integer.parseInt(CONSTANTS.HeaderHeight());
			
			//Listen for page content object selection changes
			selectionManager.addListener(this);
			
			//Initialisations
			imageLoader = new DocumentImageLoader();
			pageLayout = new PageLayoutC();
			pageSync = new PageSyncManager(null, pageLayout);
			pageView = new PageScrollView(pageLayout, imageLoader, selectionManager, true, false);
			pageContentRendererPlugin = new PageContentRendererPlugin();
			pageView.getRenderer().addPlugin(pageContentRendererPlugin);
			pageContentRendererPlugin.setFill(false);
			pageView.getRenderer().addPlugin(selectionRendererPlugin = new ContentSelectionRendererPlugin());
			pageView.addZoomListener(this);
			pageView.setMinZoom(0.05);
			pageSync.addListener(pageView);
			pageSync.addListener(this);
			imageLoader.addListener(pageView);
			imageLoader.addListener(this);
			
			//Main panel (child panels vertically arranged)
			mainPanel = new VerticalPanel();
			mainPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");  
			
			//Focus panel (required for keyboard handler)
			focusPanel = new FocusPanel(mainPanel);
			focusPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");  
			focusPanel.addKeyPressHandler(this);
			focusPanel.addKeyDownHandler(this);
			focusPanel.getElement().getStyle().setOutlineWidth(0, Unit.PX);
			RootPanel.get().add(focusPanel);
			focusPanel.setFocus(true);
			
			//Header panel at the top
			FlexTable header = new FlexTable();
			header.addStyleName("headerPanel"); 
			mainPanel.add(header);
			
			documentTitle = new Label(CONSTANTS.LoadingDocument()); 
			header.setWidget(0, 0, documentTitle);
			documentTitle.addStyleName("documentTitle"); 
			
			//Centre panel (with toolbar, page view, and region labels)
			HorizontalPanel centerPanel = new HorizontalPanel();
			mainPanel.add(centerPanel);
			centerPanel.setSize("99.8%", (Window.getClientHeight()-resizeRequiredExtraHeight)+"px");  
			
			//Toolbar panel at the left
			toolbar = createToolbar(); 
			centerPanel.add(toolbar);
			centerPanel.setCellWidth(toolbar, "1%"); 
	
			//Split panel with page view and region labels
			splitPanel = new SplitLayoutPanel();
			centerPanel.add(splitPanel);
			splitPanel.setSize("99.8%", (Window.getClientHeight()-resizeRequiredExtraHeight)+"px");  
			splitPanel.addStyleName("contentPanel"); 
	
			//Region labels
			VerticalPanel rightPanel = new VerticalPanel();
			rightPanel.setWidth("100%"); 
			splitPanel.addEast(rightPanel, Integer.parseInt(CONSTANTS.IntitialRegionLabelPanelWidth())); //Right
			// Headline
			regionTypeEditorHeading = new Label(CONSTANTS.LabelEditorHeadingCreateRegion()); 
			regionTypeEditorHeading.addStyleName("regionTypeEditorHeading"); 
			rightPanel.add(regionTypeEditorHeading);
			// Editor
			regionTypeEditor = new SimpleRegionTypeEditor(selectionManager);
			regionTypeEditor.addRegionTypeSelectionListener(this);
			selectionManager.addListener(regionTypeEditor);
			regionTypeEditor.getWidget().addStyleName("regionTypeEditor"); 
			rightPanel.add(regionTypeEditor.getWidget());
			
			//Page view
			splitPanel.add(pageView); //Centre
			pageView.asWidget().addStyleName("pageView"); 
	
			//Text content dialogue
			createTextDialog();
	
			//Resize handler (browser window resize events)
			Window.addResizeHandler(this);
	
			//Add tool icons to page view
			addToolIconsToPageView();
	
			//Authenticate user
			userManager.addListener(this);
			userManager.logOn(	Window.Location.getParameter("Appid"),  
								Window.Location.getParameter("Did"), 
								Window.Location.getParameter("Aid"), 
								Window.Location.getParameter("a")); 
			
			//Get the virtual keyboard layout from the server
			loadVirtualKeyboardLayout();
			
			logManager.logInfo(0, "onModuleLoad() finished");
		} catch (Exception exc) {
			logManager.logError(ERROR_ON_MODULE_LOAD, "Error in onModuleLoad()");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Creates the main toolbar (with 'Save', 'Revert', zoom, ...)
	 * @return Toolbar panel
	 */
	private Widget createToolbar() {
		FlexTable toolbar = new FlexTable();
		try {
			toolbar.addStyleName("toolbarPanel");  
			
			//TOP
			VerticalPanel topToolbarPanel = new VerticalPanel();
			toolbar.setWidget(0, 0, topToolbarPanel);
			toolbar.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
			topToolbarPanel.addStyleName("topToolbarPanel");  
			topToolbarPanel.addStyleName("paddedVerticalPanel");  
			topToolbarPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			topToolbarPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			
			//MIDDLE
			VerticalPanel middleToolbarPanel = new VerticalPanel();
			toolbar.setWidget(1, 0, middleToolbarPanel);
			middleToolbarPanel.addStyleName("paddedVerticalPanel");  
			middleToolbarPanel.getElement().getStyle().setProperty("margin", "auto");  
			middleToolbarPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			Label label = new Label(CONSTANTS.ToolbarSectionHeadingZoom()); 
			middleToolbarPanel.add(label);
			
			//Zoom in button
			Image img = new Image("img/zoom_in.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipZoomIn()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			zoomIn = new PushButton(img);
			middleToolbarPanel.add(zoomIn);
			zoomIn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomIn();
				}
			});
			
			//Zoom out button
			img = new Image("img/zoom_out.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipZoomOut()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			zoomOut = new PushButton(img);
			middleToolbarPanel.add(zoomOut);
			zoomOut.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomOut();
				}
			});
	
			//Zoom to fit button
			img = new Image("img/zoom_to_fit.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipZoomToFit()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			PushButton zoomFit = new PushButton(img);
			middleToolbarPanel.add(zoomFit);
			zoomFit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomToFitPage();
				}
			});
			
			//Zoom to width button
			img = new Image("img/zoom_to_width.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipZoomToWidth()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			PushButton zoomWidth = new PushButton(img);
			middleToolbarPanel.add(zoomWidth);
			zoomWidth.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					pageView.zoomToFit(pageLayout.getWidth(), 1, false);
					pageView.centerRectangle(0, 0, pageLayout.getWidth()-1, pageLayout.getHeight()-1, false);
				}
			});
			
			label = new Label(CONSTANTS.ToolbarSectionHeadingContent()); 
			middleToolbarPanel.add(label);
			
			//Active page content type selection box
			if (ENABLE_CONTENT_TYPE_SELECTOR) {
				VerticalPanel contentTypePanel = new VerticalPanel();
				contentTypePanel.addStyleName("contentTypePanel"); 
				middleToolbarPanel.add(contentTypePanel);
				showRegions = new ToggleButton(CONSTANTS.ContentToggleButtonCaptionRegions()); 
				contentTypePanel.add(showRegions);    
				showRegions.setDown(true);
				showTextLines = new ToggleButton(CONSTANTS.ContentToggleButtonCaptionTextLines()); 
				contentTypePanel.add(showTextLines);    
				showRegions.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (showRegions.isDown()) {
							loadPageContent("Region"); 
							showTextLines.setDown(false);
						} else {
							showRegions.setDown(true);
						}
					}
				});
				showTextLines.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (showTextLines.isDown()) {
							loadPageContent("TextLine"); 
							showRegions.setDown(false);
						} else {
							showTextLines.setDown(true);
						}
					}
				});
			}
			
			//Create content object button
			img = new Image("img/new_object.png"); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			img.getElement().getStyle().setMarginTop(3, Unit.PX);
			PushButton createRectRegion = new PushButton(img);
			createRectRegion.setTitle(CONSTANTS.ButtonTooltipCreateNewObject()); 
			middleToolbarPanel.add(createRectRegion);
			createRectRegion.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					CreatePageObjectTool tool = null;
					if ("Region".equals(currentPageObjectType)) {
						tool = new CreatePageObjectTool(DrawingTool.Rectangle, pageView, RegionType.TextRegion, pageSync);
						tool.setRegionSubType("other");
					} else
						tool = new CreatePageObjectTool(DrawingTool.Rectangle, pageView, currentPageObjectType, pageSync);
				}
			});
			
			//Separator
			label = new Label(""); 
			middleToolbarPanel.add(label);
	
			//Previous region
			img = new Image("img/previous.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipSelectPreviousObject()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			previousObject = new PushButton(img);
			middleToolbarPanel.add(previousObject);
			previousObject.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					selectPreviousObject();
				}
			});
			previousObject.setEnabled(false);
			
			//Next region
			img = new Image("img/next.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipSelectNextObject()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			nextObject = new PushButton(img);
			middleToolbarPanel.add(nextObject);
			nextObject.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					selectNextObject();
				}
			});
			nextObject.setEnabled(false);
			
			//Separator
			label = new Label(CONSTANTS.ToolbarSectionHeadingChanges()); 
			middleToolbarPanel.add(label);
	
			//Save button
			img = new Image("img/save.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipSaveChanges()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			PushButton saveButton = new PushButton(img);
			middleToolbarPanel.add(saveButton);
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					save();
				}
			});
			
			//Revert button
			img = new Image("img/revert.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipRevertChanges()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			revertButton = new PushButton(img);
			middleToolbarPanel.add(revertButton);
			revertButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					revert();
				}
			});
			
			//Separator
			label = new Label(""); 
			middleToolbarPanel.add(label);
	
			//Help button
			img = new Image("img/help.png"); 
			img.setTitle(CONSTANTS.ButtonTooltipShowHelp()); 
			img.getElement().getStyle().setDisplay(Display.BLOCK);
			img.getElement().getStyle().setProperty("margin", "auto");  
			PushButton helpButton = new PushButton(img);
			middleToolbarPanel.add(helpButton);
			helpButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showHelp();
				}
			});
			
			//BOTTOM
			VerticalPanel bottomToolbarPanel = new VerticalPanel();
			toolbar.setWidget(2, 0, bottomToolbarPanel);
			toolbar.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
			bottomToolbarPanel.addStyleName("paddedVerticalPanel");  
			bottomToolbarPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			bottomToolbarPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		} catch (Exception exc) {
			logManager.logError(ERROR_CREATING_TOOLBAR, "Error creating toolbar");
			exc.printStackTrace();
		}
		
		return toolbar;
	}
	
	/**
	 * Creates the dialogue to view/edit text content.
	 */
	private void createTextDialog() {
		textDialog = new DialogBox();
		textDialog.setModal(false);
		try {
			textDialog.ensureDebugId("cwDialogBox"); 
			textDialog.setText("Text Content"); 
			
			VerticalPanel dialogContents = new VerticalPanel();
			dialogContents.setSpacing(4);
			textDialog.setWidget(dialogContents);
	
			textContentView = new TextContentView(false);
			textContentView.getWidget().getElement().setId("TextContentView"); 
			dialogContents.add(textContentView.getWidget());
			selectionManager.addListener(textContentView);
			textContentView.getWidget().addStyleName("textContentView");
			textContentView.addChangeListener(this);
			
			//Virtual keyboard
			virtualKeyboard = new VirtualKeyboard(false);
			virtualKeyboard.addListener(textContentView);
			dialogContents.add(virtualKeyboard);
			
		    // Add buttons at the bottom of the dialog
			HorizontalPanel textDlgbuttons = new HorizontalPanel();
			textDlgbuttons.setSpacing(5);
			dialogContents.add(textDlgbuttons);
		    dialogContents.setCellHorizontalAlignment(textDlgbuttons, HasHorizontalAlignment.ALIGN_RIGHT);
		    
		    /*Button applyButton = new Button(CONSTANTS.TextDialogButtonCaptionApply(), new ClickHandler() { 
		    	public void onClick(ClickEvent event) {
		    		saveTextContent();
		        }
		    });
		    textDlgbuttons.add(applyButton);
		    */
		    Button closeButton = new Button(CONSTANTS.TextDialogButtonCaptionClose(), new ClickHandler() { 
		    	public void onClick(ClickEvent event) {
		    		saveTextContent();
		    		textDialog.hide();
		        }
		    });
		    textDlgbuttons.add(closeButton);
		} catch (Exception exc) {
			logManager.logError(ERROR_CREATING_TEXT_DIALOG, "Error creating text dialogue");
			exc.printStackTrace();
		}
	}
	
	@Override
	public void logOnSuccessful(UserManager userManager) {
		try {
			//Load image and content
			imageLoader.loadImage(userManager.getDocumentImageWebServiceUrl());
			pageSync.loadContentObjectsAsync("Region"); 
			pageSync.loadPageId();
		} catch (Exception exc) {
			logManager.logError(ERROR_LOADING_DOCUMENT_DATA, "Error on triggering loading the document data");
			exc.printStackTrace();
		}
	}

	@Override
	public void logOnFailed(UserManager userManager) {
		Window.alert(CONSTANTS.ErrorMessageLogonFailed()); 
	}
	
	/**
	 * Loads the page content objects of a specific type (contentType drop-down box) from the server.
	 */
	private void loadPageContent(String type) {
		try {
			selectionManager.clearSelection();
			
			if (showRegions != null)
				showRegions.setDown("Region".equals(type));
			if (showTextLines != null)
				showTextLines.setDown("TextLine".equals(type)); 
	
			currentPageObjectType = type;
			pageSync.loadContentObjectsAsync(type);
		} catch (Exception exc) {
			logManager.logError(ERROR_LOADING_DOCUMENT_DATA, "Error on triggering loading the document page content");
			exc.printStackTrace();
		}
	}

	@Override
	public void onResize(ResizeEvent event) {
		try {
			mainPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");  
			focusPanel.setSize("99.8%", (Window.getClientHeight()-1)+"px");  
			splitPanel.setSize("99.8%", (Window.getClientHeight()-resizeRequiredExtraHeight)+"px");
		} catch (Exception exc) {
			logManager.logError(ERROR_ON_RESIZE, "Error on window resize");
			exc.printStackTrace();
		}
	}

	@Override
	public void imageLoaded() {
		try {
			pageLayout.setWidth(imageLoader.getOriginalImageWidth());
			pageLayout.setHeight(imageLoader.getOriginalImageHeight());
			
			pageView.zoomToFitPage();
			
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error after document page image has been loaded");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Adds tool controls to the page view.
	 */
	void addToolIconsToPageView() {
		try {
			//Move region tool
			pageView.addHoverWidget(new MoveRegionTool("img/move.png", selectionManager, pageSync, true, true)); 
	
			//Resize tools
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_LEFT, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_RIGHT, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_TOP, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_BOTTOM, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_TOP_LEFT, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_TOP_RIGHT, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_BOTTOM_LEFT, "img/resize.png", selectionManager, pageSync, true)); 
			pageView.addHoverWidget(new ResizeRegionTool(ResizeRegionTool.TYPE_BOTTOM_RIGHT, "img/resize.png", selectionManager, pageSync, true)); 
			
			//Toolbar for selected objects
			ContentObjectToolbar objectToolbar = new ContentObjectToolbar(selectionManager);
			pageView.addHoverWidget(objectToolbar);
			
			//Edit object outline tool
			editOutlineToolbarButton = new ContentObjectToolbarButton("img/editOutline.png", CONSTANTS.ButtonTooltipEditOutline());  
			objectToolbar.add(editOutlineToolbarButton);
			editOutlineToolbarButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ContentObjectC selObj = null;
					if (selectionManager.getSelection().size() == 1) {
						selObj = selectionManager.getSelection().iterator().next();
						pageContentRendererPlugin.enableHighlight(false);
						pageContentRendererPlugin.setGreyedOut(true);
						selectionRendererPlugin.enable(false);
						final SelectionFocusRendererPlugin focusRendererPlugin = new SelectionFocusRendererPlugin();
						pageView.getRenderer().addPlugin(focusRendererPlugin);
						EditOutlineTool tool = new EditOutlineTool(selObj, pageView, selectionManager, pageSync); 
						pageView.setTool(tool);
						tool.addListener(new PageViewToolListener() {
							@Override
							public void onToolFinished(PageViewTool tool, boolean success) {
								pageContentRendererPlugin.enableHighlight(true);
								pageContentRendererPlugin.setGreyedOut(false);
								selectionRendererPlugin.enable(true);
								pageView.getRenderer().removePlugin(focusRendererPlugin);
								pageView.getRenderer().refresh();
							}
						});
					}
				}
			});
	
			//Edit/view text tool
			editTextToolbarButton = new ContentObjectToolbarButton("img/editText.png", CONSTANTS.ButtonTooltipEditText());  
			objectToolbar.add(editTextToolbarButton);
			editTextToolbarButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
			        textDialog.center();
			        textDialog.show();
				}
			});
			
			//Delete object tool
			deleteObjectToolbarButton = new ContentObjectToolbarButton("img/delete.png", CONSTANTS.ButtonTooltipDeleteObject());   
			objectToolbar.add(deleteObjectToolbarButton);
			deleteObjectToolbarButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ContentObjectC selObj = null;
					if (selectionManager.getSelection().size() == 1) {
						selObj = selectionManager.getSelection().iterator().next();
					}
					DeleteContentObjectTool.run(pageView.getViewPanel(), deleteObjectToolbarButton.asWidget(), 
												pageLayout, selObj, pageSync, selectionManager);
				}
			});
			
			objectToolbar.refresh();
		} catch (Exception exc) {
			logManager.logError(ERROR_CREATING_LAYOUT_OBJECT_TOOLS, "Error creating the layout object tools");
			exc.printStackTrace();
		}
	}

	@Override
	public void zoomChanged(double newZoomFactor, double oldZoomFactor, boolean isMinZoom, boolean isMaxZoom) {
		//Enable/disable zoom buttons
		if (zoomIn != null)
			zoomIn.setEnabled(!isMaxZoom);
		if (zoomOut != null)
			zoomOut.setEnabled(!isMinZoom);
	}
	
	/**
	 * Shows the help dialogue.
	 */
	private void showHelp() {
		try {
			final DialogBox helpDialog = new DialogBox(true);
			helpDialog.setGlassEnabled(true);
	
			//Renders "help.html" in an iframe.
			Frame frame = new Frame("Help.html"); 
			frame.setWidth(CONSTANTS.HelpDialogWidth()); 
			frame.setHeight(CONSTANTS.HelpDialogWidth()); 
			frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
			helpDialog.setWidget(frame);
			
			helpDialog.center();
			
			helpDialog.show();
		} catch (Exception exc) {
			logManager.logError(ERROR_SHOWING_HELP_DIALOG, "Error on trying to display the help dialogue");
			exc.printStackTrace();
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		try {
			int code = event.getUnicodeCharCode(); 
			if (code == '\u002B')
				pageView.zoomIn();
			else if (code == '\u2212' || code == '\u2010' || code == '\u002D')
				pageView.zoomOut();
			else if (code == '\u2212' || code == '\u2010' || code == '\u002D')
				pageView.zoomOut();
			//else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_PAGEDOWN)
			//	selectNextObject();
			//else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_PAGEUP)
			//	selectPreviousObject();
			//else
			//	Window.alert(""+code);
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error in onKeyPress()");
			exc.printStackTrace();
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		try {
			if (event.isRightArrow())
				pageView.getScrollPanel().scroll(10, 0);
			else if (event.isLeftArrow())
				pageView.getScrollPanel().scroll(-10, 0);
			else if (event.isUpArrow())
				pageView.getScrollPanel().scroll(0, -10);
			else if (event.isDownArrow())
				pageView.getScrollPanel().scroll(0, 10);
			else if (event.getNativeKeyCode() == KeyCodes.KEY_PAGEDOWN)
				selectNextObject();
			else if (event.getNativeKeyCode() == KeyCodes.KEY_PAGEUP)
				selectPreviousObject();
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error in onKeyDown()");
			exc.printStackTrace();
		}
	}

	/**
	 * Saves the document layout (asynchronous).
	 */
	private void save() {
		try {
			pageSync.save();
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error triggering save()");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Revert all changes made in this session (or since the last 'save').
	 */
	private void revert() {
		showRevertConfiramtionDialog(mainPanel, revertButton, pageSync);
	}
	
	/**
	 * Shows a dialogue asking for confirmation to revert all changes. Proceeds with 'revert' if confirmed by the user.
	 */
	private void showRevertConfiramtionDialog(Panel parent, UIObject showRelativeTo, final PageSyncManager pageSync) {
		try {
			final DialogBox confirmationDialog = new DialogBox();
			
			final VerticalPanel vertPanel = new VerticalPanel();
			confirmationDialog.add(vertPanel);
			
			Label confirmLabel = new Label(CONSTANTS.MessagePromptRevertChanges()); 
			vertPanel.add(confirmLabel);
			
			final HorizontalPanel horPanel = new HorizontalPanel();
			horPanel.setWidth("100%"); 
			horPanel.setSpacing(5);
			horPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.LTR));
			vertPanel.add(horPanel);
			
			//Cancel button
			Button buttonCancel = new Button(CONSTANTS.RevertDialogButtonCaptionCancel()); 
			horPanel.add(buttonCancel);
			buttonCancel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					confirmationDialog.hide();
				}
			});
			
			//Revert button
			Button buttonDelete = new Button(CONSTANTS.RevertDialogButtonCaptionRevert()); 
			horPanel.add(buttonDelete);
			buttonDelete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
					pageSync.revertChanges();
					confirmationDialog.hide();
				}
			});
			
			parent.add(confirmationDialog);
			if (showRelativeTo != null)
				confirmationDialog.showRelativeTo(showRelativeTo);
			else
				confirmationDialog.show();
		} catch (Exception exc) {
			logManager.logError(ERROR_REVERT_CONFIRMATION_DIALOG, "Error on diplaying the revert confiramtion dialogue");
			exc.printStackTrace();
		}
	}

	@Override
	public void contentLoaded(String contentType) {
		try {
			//Mark all content objects as 'read-only'
			if (makePageContentObjectsReadOnly) {
				List<ContentObjectC> contentObjects = pageLayout.getContent(contentType);
				if (contentObjects != null) {
					for (Iterator<ContentObjectC> it = contentObjects.iterator(); it.hasNext(); ) {
						it.next().setReadOnly(true);
					}
				}
				makePageContentObjectsReadOnly = false;
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error post-processing loaded page content");
			exc.printStackTrace();
		}
	}

	@Override
	public void pageIdLoaded(String id) {
		try {
			if (id != null && !id.isEmpty())
				documentTitle.setText(CONSTANTS.DocumentPageHeadingPrefix() + id); 
			else 
				documentTitle.setText(CONSTANTS.DocumentPageHeadingUnknownDocument()); 
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error setting the document title");
			exc.printStackTrace();
		}
	}

	@Override
	public void contentObjectAdded(ContentObjectSync syncObj, ContentObjectC localObj) {
		try {
			//Select the newly created object
			selectionManager.setSelection(localObj);
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error selecting newly created content object");
			exc.printStackTrace();
		}
	}

	@Override
	public void contentObjectDeleted(ContentObjectC object) {
	}

	@Override
	public void textContentSynchronized(ContentObjectC object) {
	}

	@Override
	public void objectOutlineSynchronized(ContentObjectC object) {
	}

	@Override
	public void pageFileSaved() {
		Window.alert(CONSTANTS.MessageSaveSuccessful()); 
	}

	@Override
	public void textChanged() {
	}
	
	@Override
	public void preSelectionHandlingOfTextContentView(SelectionManager manager) {
		try {
			if (manager != null && manager.getPreviousSelection().size() == 1) {
				ContentObjectC previouslySelectedObject = manager.getPreviousSelection().iterator().next();
				//Save changed text
				if (previouslySelectedObject != null && !previouslySelectedObject.isReadOnly()) {
					saveTextContent(previouslySelectedObject);
				}
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error in preSelectionHandlingOfTextContentView()");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Applies the text from the text dialogue to the currently selected object.
	 */
	private void saveTextContent() {
		try {
			Set<ContentObjectC> selObjects = selectionManager.getSelection();
			if (selObjects.size() == 1) {
				saveTextContent(selObjects.iterator().next());
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error saving changed text content");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Applies the text from the text dialogue to the specified object.
	 */
	private void saveTextContent(ContentObjectC obj) {
		try {
			if (textDialog.isShowing()) {
				obj.setText(textContentView.getText());
				pageSync.syncTextContent(obj);
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error saving changed text content");
			exc.printStackTrace();
		}
	}

	/**
	 * Loads the keys for the virtual keyboard (text dialogue) from an XML file.
	 */
	private void loadVirtualKeyboardLayout() {
	    AsyncCallback<VariableMap> callback = new AsyncCallback<VariableMap>() {
	    	public void onFailure(Throwable caught) {
	    		showErrorDialogue(CONSTANTS.ErrorMessageLoadingVirtualKeyboardFailed());
	    	}

	    	public void onSuccess(VariableMap variables) {
	    		virtualKeyboard.addLayout(variables);
	    	}
	    };
	    
	    //String url = GWT.getModuleBaseURL() + "special_characters.xml"; //Didn't work
	    //String baseUrl = GWT.getModuleBaseURL();
	    
	    //String url = baseUrl+"data/special_characters.xml";
	    
	    try {
	    	keyboardSyncService.loadVariables(CONSTANTS.VirtualKeyboardSourceUrl(), callback);
	    } catch(Exception exc) {
	    	exc.printStackTrace();
	    	CONSTANTS.ErrorMessageLoadingVirtualKeyboardFailed();
	    }
	}

	@Override
	public void regionTypeSelected(RegionType selectedType, String selectedSubType) {
		try {
			//Region selected
			if (selectionManager.getSelection().size() == 1 
					&& (selectionManager.getSelection().iterator().next().getType() instanceof RegionType)) {
				ContentObjectC selObj = selectionManager.getSelection().iterator().next();
	
				pageSync.syncRegionType(selObj, selectedType, selectedSubType);
			} 
			else { //No region selected -> Activate create tool
				//Switch to regions if necessary
				if (!"Region".equals(currentPageObjectType)) { 
					loadPageContent("Region"); 
				}
				
				//Activate tool
				if (selectedType == null)
					selectedType = RegionType.UnknownRegion;
				CreatePageObjectTool tool = new CreatePageObjectTool(DrawingTool.Rectangle, pageView, selectedType, pageSync);
				tool.setRegionSubType(selectedSubType);
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_REGION_TYPE_SELECTION_CHANGE, "Error after a new region type has been selected");
			exc.printStackTrace();
		}
	}

	@Override
	public void regionTypeSynchronized(ContentObjectC remoteObject, ArrayList<String> childObjectsToDelete) {
		try {
			//Find the local object
			ContentObjectC localObject = pageLayout.findContentObject(remoteObject.getId());
			
			//Copy all properties
			localObject.setType(remoteObject.getType());
			localObject.setAttributes(remoteObject.getAttributes());
			
			System.out.println("Type changed: "+remoteObject.getType().getName()); 
			String subtype = "none"; 
			VariableMap attrs = localObject.getAttributes();
			if (attrs != null) {
				Variable attr = attrs.get("type"); 
				if (attr != null)
					subtype = ""+attr.getValue(); 
			}
			System.out.println(" Subtype: "+subtype); 
			
			//Delete obsolete child objects if necessary
			if (childObjectsToDelete != null) {
				for (int i=0; i<childObjectsToDelete.size(); i++) {
					pageLayout.remove(pageLayout.findContentObject(childObjectsToDelete.get(i)));
				}
			}
			
			pageView.getRenderer().refresh();
			
			if (localObject.getType() != RegionType.TextRegion)
				textContentView.clear();
		} catch (Exception exc) {
			logManager.logError(ERROR_REGION_TYPE_SYNCHRONIZED, "Error on finalising a region type change");
			exc.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(SelectionManager manager) {
		try {
			selectedContentObject = null;
			
			//Update region type editor heading
			if (manager.getSelection().size() == 0) //Nothing selected
				regionTypeEditorHeading.setText(CONSTANTS.LabelEditorHeadingCreateRegion()); 
			else {
				selectedContentObject = selectionManager.getSelection().iterator().next();
				if (selectedContentObject != null && !selectedContentObject.isReadOnly())
					regionTypeEditorHeading.setText(CONSTANTS.LabelEditorHeadingChooseLabel()); 
				else
					regionTypeEditorHeading.setText(CONSTANTS.LabelEditorHeadingLabel()); 
			}
			
			if (selectedContentObject != null) {
				//Enable/disable 'edit outline' and 'delete object' buttons
				editOutlineToolbarButton.setEnabled(!selectedContentObject.isReadOnly());
				deleteObjectToolbarButton.setEnabled(!selectedContentObject.isReadOnly());
			
				//Enable/disable text edit button
				boolean enable = selectedContentObject.getType().equals(RegionType.TextRegion)
								|| 	selectedContentObject.getType().equals(LowLevelTextType.TextLine)
								|| 	selectedContentObject.getType().equals(LowLevelTextType.Word)
								|| 	selectedContentObject.getType().equals(LowLevelTextType.Glyph);
				editTextToolbarButton.setEnabled(enable);
				editTextToolbarButton.asWidget().setTitle(selectedContentObject.isReadOnly() 	? CONSTANTS.ButtonTooltipViewTextContent() 
																								: CONSTANTS.ButtonTooltipEditText());  
			}
			
			//"Previous" / "Next" button
			nextObject.setEnabled(manager.getSelection().size() == 1);
			previousObject.setEnabled(manager.getSelection().size() == 1);
		} catch (Exception exc) {
			logManager.logError(ERROR_ON_SELECTION_CHANGE, "Error on handling content object selection");
			exc.printStackTrace();
		}
	}

	@Override
	public void changesReverted() {
		try {
			selectionManager.clearSelection();
			textDialog.hide();
			pageLayout.clear(true);
			makePageContentObjectsReadOnly = true;
			if (showRegions == null || showRegions.isDown())
				loadPageContent("Region"); 
			else
				loadPageContent("TextLine");
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error on finalising reverting all changes");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Selects the object that is directly before the currently selected object (according to the sort order of the internal object list).
	 * Also centres the object in the view.
	 */
	private void selectPreviousObject() {
		try {
			if (!selectionManager.isEmpty()) {
				ContentObjectC selObj = selectionManager.getSelection().iterator().next();
				if (selObj != null) {
					ContentObjectC prevObject = pageLayout.getPreviousObject(selObj);
					selectionManager.setSelection(prevObject);
					//Centre object
					Polygon poly = prevObject.getCoords();
					if (poly != null) {
						Rect box = poly.getBoundingBox();
						pageView.centerRectangle(box.left, box.top, box.right, box.bottom, true);
					}
				}
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error on selectPreviousObject()");
			exc.printStackTrace();
		}
	}

	/**
	 * Selects the object that is directly after the currently selected object (according to the sort order of the internal object list).
	 * Also centres the object in the view.
	 */
	private void selectNextObject() {
		try {
			if (!selectionManager.isEmpty()) {
				ContentObjectC selObj = selectionManager.getSelection().iterator().next();
				if (selObj != null) {
					ContentObjectC nextObject = pageLayout.getNextObject(selObj);
					selectionManager.setSelection(nextObject);
					//Centre object
					Polygon poly = nextObject.getCoords();
					if (poly != null) {
						Rect box = poly.getBoundingBox();
						pageView.centerRectangle(box.left, box.top, box.right, box.bottom, true);
					}
				}
			}
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error on selectNextObject()");
			exc.printStackTrace();
		}
	}

	@Override
	public void contentLoadingFailed(String contentType, Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageContentLoadingFailed());
	}

	@Override
	public void pageIdLoadingFailed(Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessagePageIdLoadingFailed());
	}

	@Override
	public void contentObjectAddingFailed(ContentObjectC object,
			Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageContentObjectAddingFailed());
	}

	@Override
	public void contentObjectDeletionFailed(ContentObjectC object,
			Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageContentObjectDeletionFailed());
	}

	@Override
	public void textContentSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageTextContentSyncFailed());
	}

	@Override
	public void regionTypeSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageRegionTypeSyncFailed());
	}

	@Override
	public void objectOutlineSyncFailed(ContentObjectC object, Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageObjectOutlineSyncFailed());
	}

	@Override
	public void pageFileSaveFailed(Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessagePageFileSaveFailed());
	}

	@Override
	public void revertChangesFailed(Throwable caught) {
		showErrorDialogue(CONSTANTS.ErrorMessageRevertChangesFailed());
	}
	
	/**
	 * Shows an error message at the top of the page (can be closed; auto hide after 10 seconds)
	 * @param message Error message to display
	 */
	private void showErrorDialogue(String message) {
		try {
			if (message == null || message.isEmpty())
				return;
			
			if (errorMessageBox == null) {
				errorMessageBox = new DialogBox(false, false);
				errorMessageBox.getElement().setId("errorMessageBoxDialog");
				
				errorMessageBox.addStyleName("errorMessageBox");
			
				final HorizontalPanel panel = new HorizontalPanel();
				errorMessageBox.add(panel);
				panel.addStyleName("errorMessagePanel");
			
				errorMessage = new Label(); 
				panel.add(errorMessage);
				errorMessage.getElement().getStyle().setMarginRight(5, Unit.PX);
			
				//Close button
				Image img = new Image("img/close.png"); 
				PushButton buttonClose = new PushButton(img); 
				panel.add(buttonClose);
				buttonClose.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						errorMessageBox.hide();
					}
				});
			
				mainPanel.add(errorMessageBox);
			}
			errorMessage.setText(message);
			
			//Hide after 10 seconds (can be changed in AppConstants.properties)
			if (errorMessageTimer != null)
				errorMessageTimer.cancel();
			errorMessageTimer = new Timer() {
				public void run() {
					errorMessageBox.hide();
				}
			};
			errorMessageTimer.schedule(Integer.parseInt(CONSTANTS.ErrorMessageDisplayTimeInMilliseconds()));
			
			errorMessageBox.showRelativeTo(documentTitle);
		} catch (Exception exc) {
			logManager.logError(ERROR_UNSPECIFIED, "Error displaying the message dialogue");
			exc.printStackTrace();
		}
	}
}

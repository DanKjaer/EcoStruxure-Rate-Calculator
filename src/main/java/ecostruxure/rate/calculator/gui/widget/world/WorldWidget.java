package ecostruxure.rate.calculator.gui.widget.world;

import eu.hansolo.fx.countries.Country;

import eu.hansolo.fx.countries.evt.CountryEvt;
import eu.hansolo.fx.countries.tools.Connection;
import eu.hansolo.fx.countries.tools.Constants;
import eu.hansolo.fx.countries.tools.CountryPath;
import eu.hansolo.fx.countries.tools.Helper;
import eu.hansolo.fx.countries.tools.CLocation;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolboxfx.font.Fonts;
import eu.hansolo.toolboxfx.geom.Poi;
import eu.hansolo.toolboxfx.geom.Point;
import eu.hansolo.fx.heatmap.ColorMapping;
import eu.hansolo.fx.heatmap.HeatMap;
import eu.hansolo.fx.heatmap.HeatMapBuilder;
import eu.hansolo.fx.heatmap.Mapping;
import eu.hansolo.fx.heatmap.OpacityDistribution;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;

import java.util.*;

import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_EXITED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;


@DefaultProperty("children")
public class WorldWidget extends Region implements EvtObserver<CountryEvt<Connection>> {
    private static final double                          PREFERRED_WIDTH  = 1009;
    private static final double                          PREFERRED_HEIGHT = 665;
    private static final double                          MINIMUM_WIDTH    = 50;
    private static final double                          MINIMUM_HEIGHT   = 50;
    private static final double                          MAXIMUM_WIDTH    = 4096;
    private static final double                          MAXIMUM_HEIGHT   = 4096;
    private static final Map<Country, List<CountryPath>> COUNTRY_PATHS    = Country.getCopyOfCountryPaths();
    private              double                          width;
    private              double                          height;
    private              Pane                            pane;
    private              Group                           group;
    private              double                          scaleX;
    private              double                          scaleY;
    private              Color                           _fill;
    private              ObjectProperty<Color>           fill;
    private              Color                           _stroke;
    private              ObjectProperty<Color>           stroke;
    private              double                          _lineWidth;
    private              DoubleProperty                  lineWidth;
    private              boolean                         _hoverEnabled;
    private              BooleanProperty                 hoverEnabled;
    private              boolean                         _selectionEnabled;
    private              BooleanProperty                 selectionEnabled;
    private              Country                         _selectedCountry;
    private              ObjectProperty<Country>         selectedCountry;
    private              Country                         formerSelectedCountry;
    private              Color                           _hoverColor;
    private              ObjectProperty<Color>           hoverColor;
    private              Color                           _pressedColor;
    private              ObjectProperty<Color>           pressedColor;
    private              Color                           _selectedColor;
    private              ObjectProperty<Color>           selectedColor;
    private              ObservableList<Poi>             pois;
    private              List<Point>                     heatmapSpots;
    private              BooleanBinding                  showing;
    private              ListChangeListener<Poi>         poiListChangeListener;
    private              ListChangeListener<Connection>  connectionListChangeListener;
    // internal event handlers
    protected            EventHandler<MouseEvent>        _mouseEnterHandler;
    protected            EventHandler<MouseEvent>        _mousePressHandler;
    protected            EventHandler<MouseEvent>        _mouseReleaseHandler;
    protected            EventHandler<MouseEvent>        _mouseExitHandler;
    // exposed event handlers
    private              EventHandler<MouseEvent>        mouseEnterHandler;
    private              EventHandler<MouseEvent>        mousePressHandler;
    private              EventHandler<MouseEvent>        mouseReleaseHandler;
    private              EventHandler<MouseEvent>        mouseExitHandler;


    // ******************** Constructors **************************************
    public WorldWidget() {
        this._fill                        = Constants.FILL;
        this._stroke                      = Constants.STROKE;
        this._lineWidth                   = 1;
        this._hoverEnabled                = false;
        this._selectionEnabled            = false;
        this._selectedCountry             = null;
        this.formerSelectedCountry        = null;
        this._hoverColor                  = Color.web("#3b5b6b");
        this._pressedColor                = Color.DARKBLUE;
        this._selectedColor               = Color.web("#28596f");
        this.pois                         = FXCollections.observableArrayList();
        this.heatmapSpots                 = new ArrayList<>();

        this._mouseEnterHandler           = evt -> handleMouseEvent(evt, mouseEnterHandler);
        this._mousePressHandler           = evt -> handleMouseEvent(evt, mousePressHandler);
        this._mouseReleaseHandler         = evt -> handleMouseEvent(evt, mouseReleaseHandler);
        this._mouseExitHandler            = evt -> handleMouseEvent(evt, mouseExitHandler);

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        COUNTRY_PATHS.forEach((country, value) -> value.forEach(path -> {
            path.setFill(null == country.getFill() ? getFill() : country.getFill());
            path.getStyleClass().add("world-path");
            path.setOnMouseEntered(new WeakEventHandler<>(_mouseEnterHandler));
            path.setOnMousePressed(new WeakEventHandler<>(_mousePressHandler));
            path.setOnMouseReleased(new WeakEventHandler<>(_mouseReleaseHandler));
            path.setOnMouseExited(new WeakEventHandler<>(_mouseExitHandler));
        }));

        group = new Group();
        COUNTRY_PATHS.forEach((key, value) -> group.getChildren().addAll(value));

        pane = new Pane(group);

        setBackground(Constants.BACKGROUND);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        if (null != getScene()) {
            setupBinding();
        } else {
            sceneProperty().addListener((o1, ov1, nv1) -> {
                if (null == nv1) { return; }
                if (null != getScene().getWindow()) {
                    setupBinding();
                } else {
                    sceneProperty().get().windowProperty().addListener((o2, ov2, nv2) -> {
                        if (null == nv2) { return; }
                        setupBinding();
                    });
                }
            });
        }
    }

    private void setupBinding() {
        showing = Bindings.createBooleanBinding(() -> {
            if (getScene() != null && getScene().getWindow() != null) {
                return getScene().getWindow().isShowing();
            } else {
                return false;
            }
        }, sceneProperty(), getScene().windowProperty(), getScene().getWindow().showingProperty());

        showing.addListener(o -> {
            if (showing.get()) resize();
        });
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void setBackground(final Paint paint) {
        setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color fill) {
        if (null == this.fill) {
            _fill = fill;
            COUNTRY_PATHS.entrySet().forEach(entry -> entry.getValue().forEach(countryPath -> countryPath.setFill(null == entry.getKey().getFill() ? fill : entry.getKey().getFill())));
        } else {
            this.fill.set(fill);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<>(_fill) {
                @Override protected void invalidated() {
                    COUNTRY_PATHS.entrySet().forEach(entry -> entry.getValue().forEach(countryPath -> countryPath.setFill(null == entry.getKey().getFill() ? get() : entry.getKey().getFill())));
                }
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color stroke) {
        if (null == this.stroke) {
            _stroke = stroke;
            COUNTRY_PATHS.entrySet().forEach(entry -> entry.getValue().forEach(countryPath -> countryPath.setStroke(null == entry.getKey().getStroke() ? stroke : entry.getKey().getStroke())));
        } else {
            this.stroke.set(stroke);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override protected void invalidated() {
                    COUNTRY_PATHS.entrySet().forEach(entry -> entry.getValue().forEach(countryPath -> countryPath.setStroke(null == entry.getKey().getStroke() ? get() : entry.getKey().getStroke())));
                }
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public double getLineWidth() { return null == lineWidth ? _lineWidth : lineWidth.get(); }
    public void setLineWidth(final double lineWidth) {
        if (null == this.lineWidth) {
            _lineWidth = lineWidth;
            COUNTRY_PATHS.entrySet().forEach(entry -> entry.getValue().forEach(countryPath -> countryPath.setStrokeWidth(lineWidth)));
        } else {
            this.lineWidth.set(lineWidth);
        }
    }
    public DoubleProperty lineWidthProperty() {
        if (null == lineWidth) {
            lineWidth = new DoublePropertyBase(_lineWidth) {
                @Override protected void invalidated() {
                    COUNTRY_PATHS.entrySet().forEach(entry -> entry.getValue().forEach(countryPath -> countryPath.setStrokeWidth(get())));
                }
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "lineWidth"; }
            };
        }
        return lineWidth;
    }


    // ******************** Interaction ***************************************
    public boolean isHoverEnabled() { return null == hoverEnabled ? _hoverEnabled : hoverEnabled.get(); }
    public void setHoverEnabled(final boolean hoverEnabled) {
        if (null == this.hoverEnabled) {
            _hoverEnabled = hoverEnabled;
        } else {
            this.hoverEnabled.set(hoverEnabled);
        }
    }
    public BooleanProperty hoverEnabledProperty() {
        if (null == hoverEnabled) {
            hoverEnabled = new BooleanPropertyBase(_hoverEnabled) {
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "hoverEnabled"; }
            };
        }
        return hoverEnabled;
    }

    public boolean isSelectionEnabled() { return null == selectionEnabled ? _selectionEnabled : selectionEnabled.get(); }
    public void setSelectionEnabled(final boolean selectionEnabled) {
        if (null == this.selectionEnabled) {
            _selectionEnabled = selectionEnabled;
        } else {
            this.selectionEnabled.set(selectionEnabled);
        }
    }
    public BooleanProperty selectionEnabledProperty() {
        if (null == selectionEnabled) {
            selectionEnabled = new BooleanPropertyBase() {
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "selectionEnabled"; }
            };
        }
        return selectionEnabled;
    }

    public Country getSelectedCountry() { return null == selectedCountry ? _selectedCountry : selectedCountry.get(); }
    public void setSelectedCountry(final Country selectedCountry) {
        if (null == this.selectedCountry) {
            _selectedCountry = selectedCountry;
        } else {
            this.selectedCountry.set(selectedCountry);
        }
    }
    public ObjectProperty<Country> selectedCountryProperty() {
        if (null == selectedCountry) {
            selectedCountry = new ObjectPropertyBase<>(_selectedCountry) {
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "selectedCountry"; }
            };
            _selectedCountry = null;
        }
        return selectedCountry;
    }

    public Color getHoverColor() { return null == hoverColor ? _hoverColor : hoverColor.getValue(); }
    public void setHoverColor(final Color hoverColor) {
        if (null == this.hoverColor) {
            _hoverColor = hoverColor;
        } else {
            this.hoverColor.setValue(hoverColor);
        }
    }
    public ObjectProperty<Color> hoverColorProperty() {
        if (null == hoverColor) {
            hoverColor = new ObjectPropertyBase<>(_hoverColor) {
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "hoverColor"; }
            };
            _hoverColor = null;
        }
        return hoverColor;
    }

    public Color getPressedColor() { return null == pressedColor ? _pressedColor : pressedColor.getValue(); }
    public void setPressedColor(final Color pressedColor) {
        if (null == this.pressedColor) {
            _pressedColor = pressedColor;
        } else {
            this.pressedColor.setValue(pressedColor);
        }
    }
    public ObjectProperty<Color> pressedColorProperty() {
        if (null == pressedColor) {
            pressedColor = new ObjectPropertyBase<>(_pressedColor) {
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "pressedColor"; }
            };
            _pressedColor = null;
        }
        return pressedColor;
    }

    public Color getSelectedColor() { return null == selectedColor ? _selectedColor : selectedColor.getValue(); }
    public void setSelectedColor(final Color selectedColor) {
        if (null == this.selectedColor) {
            _selectedColor = selectedColor;
        } else {
            this.selectedColor.setValue(selectedColor);
        }
    }
    public ObjectProperty<Color> selectedColorProperty() {
        if (null == selectedColor) {
            selectedColor = new ObjectPropertyBase<>(_selectedColor) {
                @Override public Object getBean() { return WorldWidget.this; }
                @Override public String getName() { return "selectedColor"; }
            };
            _selectedColor = null;
        }
        return selectedColor;
    }

    public void setMouseEnterHandler(final EventHandler<MouseEvent> handler) { mouseEnterHandler = handler; }
    public void setMousePressHandler(final EventHandler<MouseEvent> handler) { mousePressHandler = handler; }
    public void setMouseReleaseHandler(final EventHandler<MouseEvent> handler) { mouseReleaseHandler = handler;  }
    public void setMouseExitHandler(final EventHandler<MouseEvent> handler) { mouseExitHandler = handler; }



    // ******************** EventHandling *************************************
    private void handleMouseEvent(final MouseEvent event, final EventHandler<MouseEvent> handler) {
        final CountryPath       countryPath = (CountryPath) event.getSource();
        final String            countryName = countryPath.getName();
        final Country           country     = Country.valueOf(countryName);
        final List<CountryPath> paths       = COUNTRY_PATHS.get(country);
        final EventType type = event.getEventType();
        if (MOUSE_ENTERED == type) {
            if (isHoverEnabled()) {
                Color color = isSelectionEnabled() && country.equals(getSelectedCountry()) ? getSelectedColor() : getHoverColor();
                for (SVGPath path : paths) { path.setFill(color); }
            }
        } else if (MOUSE_PRESSED == type) {
            if (isSelectionEnabled()) {
                Color color;
                if (null == getSelectedCountry()) {
                    setSelectedCountry(country);
                    color = getSelectedColor();
                } else {
                    color = null == getSelectedCountry().getFill() ? getFill() : getSelectedCountry().getFill();
                }
                for (SVGPath path : COUNTRY_PATHS.get(getSelectedCountry())) { path.setFill(color); }
            } else {
                if (isHoverEnabled()) {
                    for (SVGPath path : paths) { path.setFill(getPressedColor()); }
                }
            }
        } else if (MOUSE_RELEASED == type) {
            Color color;
            if (isSelectionEnabled()) {
                if (formerSelectedCountry == country) {
                    setSelectedCountry(null);
                    color = null == country.getFill() ? getFill() : country.getFill();
                } else {
                    setSelectedCountry(country);
                    color = getSelectedColor();
                }
                formerSelectedCountry = getSelectedCountry();
            } else {
                color = getHoverColor();
            }
            if (isHoverEnabled()) {
                for (SVGPath path : paths) { path.setFill(color); }
            }
        } else if (MOUSE_EXITED == type) {
            if (isHoverEnabled()) {
                Color color = isSelectionEnabled() && country.equals(getSelectedCountry()) ? getSelectedColor() : getFill();
                for (SVGPath path : paths) {
                    path.setFill(null == country.getFill() || country == getSelectedCountry() ? color : country.getFill());
                }
            }
        }

        if (null != handler) handler.handle(event);
    }



    // ******************** Layout *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    private void resize() {
        resizePane();
    }

    private void resizePane() {
        width  = getWidth()- getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (Constants.ASPECT_RATIO * width > height) {
            width = 1 / (Constants.ASPECT_RATIO / height);
        } else if (1 / (Constants.ASPECT_RATIO / height) > width) {
            height = Constants.ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            scaleX = width / pane.getWidth();
            scaleY = height / pane.getHeight();

            double relocateX = (getWidth() - pane.getWidth()) * 0.5;
            double relocateY = (getHeight() - pane.getHeight()) * 0.5;


            if (Math.abs(scaleX - pane.getScaleX()) > 50 || Math.abs(scaleY - pane.getScaleY()) > 50) {
                pane.setCache(true);
                pane.setCacheHint(CacheHint.SCALE);
            }

            pane.setScaleX(scaleX);
            pane.setScaleY(scaleY);
            pane.setCache(false);

            pane.relocate(relocateX, relocateY);
        }
    }

    @Override
    public void handle(CountryEvt<Connection> connectionCountryEvt) {

    }

    private List<Country> selectedCountries = new ArrayList<>();
    public void highlightGeography(List<Country> countries) {
        clearHighlight();
        selectedCountries = countries;
        for (Country country : countries) {
            List<CountryPath> paths = COUNTRY_PATHS.get(country);
            if (paths != null) {
                for (CountryPath path : paths) {
                    path.setFill(Color.valueOf("#3d6638"));
                }
            }
        }
    }

    public void clearHighlight() {
        for (Country country : selectedCountries) {
            List<CountryPath> paths = COUNTRY_PATHS.get(country);
            if (paths != null) {
                for (CountryPath path : paths) {
                    path.setFill(Constants.FILL);
                }
            }
        }
    }
}
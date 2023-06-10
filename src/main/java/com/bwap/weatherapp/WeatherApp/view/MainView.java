package com.bwap.weatherapp.WeatherApp.view;

import com.bwap.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;


@SpringUI(path ="")
public class MainView<cityName> extends UI {
    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLyout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button searchButton;
    private Label location ;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label feelsLike;
    private Image iconImg;
    private HorizontalLayout dashboard;
    private HorizontalLayout mainDescriptionLayout;
    private Image logo;
    private HorizontalLayout footer;

    private VerticalLayout citydescription;

    String output;


    @Override
    protected void init(VaadinRequest vaadinRequest) {
       mainLayout();
       setHeader();
       setLogo();
       setForm();
       dashBoardTitle();
       dashBoardDetails();
       searchButton.addClickListener(clickEvent -> {
           if(!cityTextField.getValue().equals("")){
               try {
                   updateUI();
               } catch (JSONException e) {
                   throw new RuntimeException(e);
               }
           }else{
               Notification.show("Enter the City Name");
           }
       });
    }

    private void updateUI() throws JSONException {
        String city = cityTextField.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if(unitSelect.getValue().equals("F")){
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit="\u00b0"+"F";
        }else{
            weatherService.setUnit("metric");
            defaultUnit = "\u00b0"+"C";
            unitSelect.setValue("C");
        }

        location.setValue("Currently in "+city);
        JSONObject mainObj= weatherService.returnMainObject();
        int temp = mainObj.getInt("temp");
        currentTemp.setValue(temp+defaultUnit);

        //Getting Icon From the API
        String iconCode= null;
        String weatherDescriptionNew = null;
        JSONArray jsonArray = weatherService.returnWeatherArray();
        for (int i=0; i<jsonArray.length();i++){
            JSONObject weatherObj = jsonArray.getJSONObject(i);
            iconCode=weatherObj.getString("icon");
            weatherDescriptionNew=weatherObj.getString("description");
            output = weatherDescriptionNew.substring(0, 1).toUpperCase() + weatherDescriptionNew.substring(1);
        }
        iconImg.setSource(new ExternalResource("https://openweathermap.org/img/wn/"+iconCode+"@2x.png"));

        weatherDescription.setValue("Description: "+output);
        weatherMin.setValue("Min_Temp: "+weatherService.returnMainObject().getInt("temp_min")+unitSelect.getValue());
        weatherMax.setValue("Max_Temp: "+weatherService.returnMainObject().getInt("temp_max")+unitSelect.getValue());
        pressureLabel.setValue("Pressure: "+weatherService.returnMainObject().getInt("pressure"));
        humidityLabel.setValue("Humidity: "+weatherService.returnMainObject().getInt("humidity"));
        feelsLike.setValue("Feels_Like: "+weatherService.returnMainObject().getDouble("feels_like")+unitSelect.getValue());
        windSpeedLabel.setValue("Wind: "+weatherService.returnWindObject().getInt("speed"));



        mainLyout.addComponents(dashboard,citydescription,mainDescriptionLayout);
    }

    private void dashBoardDetails() {

        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        //description
        VerticalLayout description = new VerticalLayout();
        description.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //weather description
        weatherDescription = new Label("Description: Clear Skies");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);

        citydescription = new VerticalLayout();
        citydescription.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        citydescription.addComponents(weatherDescription);

        weatherMin = new Label("Min: -23");
        description.addComponents(weatherMin);

        weatherMax = new Label("Max:23");
        description.addComponents(weatherMax);

        pressureLabel = new Label("Pressure: 231Pa");
        description.addComponents(pressureLabel);

        VerticalLayout pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);



        humidityLabel = new Label("Humidity: 10");
        pressureLayout.addComponents(humidityLabel);

        windSpeedLabel = new Label("Wind: 10");
        pressureLayout.addComponents(windSpeedLabel);

        feelsLike = new Label("Feels Like: -8");
        pressureLayout.addComponents(feelsLike);

        mainDescriptionLayout.addComponents(citydescription,description,pressureLayout);

    }

    private void dashBoardTitle() {
        dashboard = new HorizontalLayout();
        dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        location = new Label("Currently In New York");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LIGHT);

        //set temp for the city
        currentTemp = new Label("-5C");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.setStyleName(ValoTheme.LABEL_H1);

        dashboard.addComponents(location,iconImg,currentTemp);

    }

    private void setForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        //Selection Component
        unitSelect=new NativeSelect<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponents(unitSelect);

        //CityName
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponents(cityTextField);

        //Search Button
        searchButton = new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(searchButton);

        mainLyout.addComponents(formLayout);

    }


    private void mainLayout() {
        iconImg = new Image();
        mainLyout = new VerticalLayout();
        mainLyout.setWidth("100%");
        mainLyout.setSpacing(true);
        mainLyout.setMargin(true);
        mainLyout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(mainLyout);
    }

    private void setHeader(){
        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather APP using OpenWeatherAPI");
        header.addComponents(title);

        mainLyout.addComponents(header);
    }
    private void setLogo() {
        HorizontalLayout logo = new HorizontalLayout();
        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image img= new Image("This is the WEATHER API",new ClassResource("/static/logo.png"));
        logo.setWidth("200px");
        logo.setHeight("240px");

        logo.addComponents(img);
        mainLyout.addComponents(logo);
    }

    private void setFooter(){
        HorizontalLayout footer = new HorizontalLayout();
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather APP using OpenWeatherAPI");
        footer.addComponents(title);

        mainLyout.addComponents(footer);
    }
}
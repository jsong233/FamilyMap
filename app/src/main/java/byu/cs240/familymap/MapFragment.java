package byu.cs240.familymap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;
import java.util.Map;

import model.*;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    private DataCache data;
    private TextView mapTextName;
    private TextView mapTextEvent;
    private TextView mapTextAddress;
    private ImageView mapGenderIcon;

//    public static final String PERSON_KEY = "PersonIDKey";
//    public static final String EVENT_KEY = "EventIDKey";


    float birthEventColor = BitmapDescriptorFactory.HUE_RED;
    float marriageEventColor = BitmapDescriptorFactory.HUE_YELLOW;
    float deathEventColor = BitmapDescriptorFactory.HUE_VIOLET;
    float defaultEventColor = BitmapDescriptorFactory.HUE_BLUE;
    float defaultLineWidth = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapTextName = view.findViewById(R.id.mapTextName);
        mapTextEvent = view.findViewById(R.id.mapTextEvent);
        mapTextAddress = view.findViewById(R.id.mapTextAddress);
        mapGenderIcon = view.findViewById(R.id.mapGenderIcon);

        data = DataCache.getInstance();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        map.setOnMapLoadedCallback(this);

        // Add markers for all the events associated with this user
        Map<String, Event> events = data.getEvents();
        markAllEvents(events);

        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        DataCache data = DataCache.getInstance();

        Event selectedEvent = (Event) marker.getTag();
        Person selectedPerson = data.getPersonById(selectedEvent.getPersonID());

        String nameText = selectedPerson.getFirstName() + " " + selectedPerson.getLastName();
        String eventText =  selectedEvent.getEventType().toUpperCase() + " (" + selectedEvent.getYear() + ")";
        String addressText = selectedEvent.getCity() + ", " + selectedEvent.getCountry();

        Drawable genderIcon;
        if (selectedPerson.getGender().equals("f")) {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                    .colorRes(R.color.female_icon).sizeDp(40);
        } else {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                    .colorRes(R.color.male_icon).sizeDp(40);
        }

        mapTextName.setText(nameText);
        mapTextEvent.setText(eventText);
        mapTextAddress.setText(addressText);
        mapGenderIcon.setImageDrawable(genderIcon);

        drawLifeEvents(selectedPerson, selectedEvent);

        return false;
    }


    private void markAllEvents(Map<String, Event> events) {
        for(Map.Entry<String, Event> entry: events.entrySet()) {
            Event event = entry.getValue();

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(defaultEventColor)));
            marker.setTag(event);
        }
    }

    private void drawLifeEvents(Person selectedPerson, Event selectedEvent) {

        List<Event> lifeEvents = data.getPersonEvents(selectedPerson.getPersonID());

        for (Event event : lifeEvents) {
            drawLine(selectedEvent, event, defaultEventColor, defaultLineWidth);
        }
    }

    private void drawLine(Event startEvent, Event endEvent, float googleColor, float width) {

        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color((int)googleColor)
                .width(width);

        Polyline line = map.addPolyline(options);
    }
}
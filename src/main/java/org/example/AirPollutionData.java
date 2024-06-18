package org.example;

import java.util.List;

public class AirPollutionData {
    public List<ListElement> list;

    public class ListElement {
        public Main main;
        public Components components;

        public class Main {
            public int aqi;
        }

        public class Components {
            public double co;
            public double no2;
            public double o3;
            public double so2;
            public double pm2_5;
            public double pm10;
        }
    }
}
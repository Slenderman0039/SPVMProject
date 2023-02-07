package it.spvm.progetto_2022_2023_fx.utils;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;

import java.time.*;

public class CustomCalendar extends Calendar {


    public CustomCalendar() {

    }

    //Crea gli eventi nel calendario, nome evento: 'Turni' includendo la data che mi necessita
    public void createEntries(LocalDate data_turno, LocalTime start_turno, LocalTime stop_turno, String servizio) {
        Entry<?> entry = new Entry<>();
        entry.setTitle("Turno");
        entry.setInterval(data_turno,start_turno,data_turno,stop_turno);
        entry.setLocation(servizio);
        entry.setCalendar(this);
    }
    }


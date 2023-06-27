package ru.bfad.bfaApp.bots.botsApi;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.bfad.bfaApp.bots.dto.BelongFromAgent;
import ru.bfad.bfaApp.bots.dto.BookingFromAgent;
import ru.bfad.bfaApp.bots.dto.ShowingForAgent;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class ClientDataCache {
    private Map<Long, BelongFromAgent> belongClients = new HashMap<>();
    private Map<Long, BookingFromAgent> bookingClients = new HashMap<>();
    private Map<Long, ShowingForAgent> showingClients = new HashMap<>();

    public BelongFromAgent getBelong (Long id){
        BelongFromAgent data = belongClients.get(id);
        if(data == null) data = new BelongFromAgent();
        return data;
    }

    public void setBelong (Long id, BelongFromAgent client){
        belongClients.put(id, client);
    }

    public BookingFromAgent getBooking (Long id){
        BookingFromAgent data = bookingClients.get(id);
        if(data == null) data = new BookingFromAgent();
        return data;
    }

    public void setBooking (Long id, BookingFromAgent client){
        bookingClients.put(id, client);
    }

    public ShowingForAgent getShowing (Long id){
        ShowingForAgent data = showingClients.get(id);
        if(data == null) data = new ShowingForAgent();
        return data;
    }

    public void setShowing (Long id, ShowingForAgent client){
        showingClients.put(id, client);
    }

}

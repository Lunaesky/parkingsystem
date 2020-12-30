package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import org.mockito.Mock;


public class FareCalculatorService {

    private TicketDAO ticketDAO;

    public FareCalculatorService(TicketDAO ticketDAO) {

        this.ticketDAO = ticketDAO;
    }


    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = (outHour - inHour) / 3600000;
        float coeff = 1;
        if (duration < 0.50) {
            coeff = 0;
        } else if (ticketDAO.getTicket(ticket.getVehicleRegNumber()) != null) {
            if (ticketDAO.getTicket(ticket.getVehicleRegNumber()).getOutTime() != null)
                coeff = 0.95f;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR) * coeff);
                break;
            }
            case BIKE: {
                ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR) * coeff);
                break;
            }
            default:
                throw new IllegalArgumentException("type de parking inconnu");
        }

    }
}
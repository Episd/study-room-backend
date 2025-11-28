package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.Seat;

public interface SeatService {
    public boolean updateSeatStatus(Seat seat);
    public Seat checkSeatInformation(String seatId);
    public boolean addSeat(Seat seat);
    public boolean deleteSeat(String seatId);
}

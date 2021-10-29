package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResendWorker implements Runnable{
    private CommWorker communicator;

    @Override
    public void run() {
        communicator.resend();
    }
}

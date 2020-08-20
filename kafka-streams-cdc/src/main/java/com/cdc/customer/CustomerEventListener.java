package com.cdc.customer;

public interface CustomerEventListener {
  void onData(CustomerDto customerDto);

  void processComplete();
}

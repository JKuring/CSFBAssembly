package com.eastcom.csfb.storm.base.pubsub;

public interface PubSubSubscriber {

    public void onPubSubMessage(String channel, String message);

}

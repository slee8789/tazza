package com.leesc.tazza.ui.lobby;


import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.leesc.tazza.R;
import com.leesc.tazza.data.DataManager;
import com.leesc.tazza.data.model.Room;
import com.leesc.tazza.di.provider.ResourceProvider;
import com.leesc.tazza.receiver.WifiDirectReceiver;
import com.leesc.tazza.service.WifiService;
import com.leesc.tazza.ui.base.BaseViewModel;
import com.leesc.tazza.utils.rx.RxEventBus;
import com.leesc.tazza.utils.rx.SchedulerProvider;

public class LobbyViewModel extends BaseViewModel<LobbyNavigator> {

    private SchedulerProvider schedulerProvider;
    private Context context;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiService wifiService;

    public LobbyViewModel(DataManager dataManager,
                          SchedulerProvider schedulerProvider,
                          Context context,
                          WifiP2pManager wifiP2pManager,
                          WifiP2pManager.Channel channel,
                          WifiService wifiService,
                          WifiDirectReceiver wifiDirectReceiver,
                          ResourceProvider resourceProvider) {

        super(dataManager, schedulerProvider);
        this.context = context;
        this.schedulerProvider = schedulerProvider;
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.wifiService = wifiService;

        discover();

        getCompositeDisposable().add(RxEventBus.getInstance().getEvents(WifiP2pDeviceList.class)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                        peers -> {
                            Log.d("lsc", "LobbyViewModel peers " + ((WifiP2pDeviceList) peers).getDeviceList());
                            getNavigator().onRepositoriesChanged(Stream.of(((WifiP2pDeviceList) peers).getDeviceList())
                                    .map(wifiP2pDevice -> new Room(wifiP2pDevice.deviceName, wifiP2pDevice.deviceAddress))
                                    .filter(room -> room.getDeviceName().contains(resourceProvider.getString(R.string.key_room_prefix)))
                                    .collect(Collectors.toList())
                            );
                        }
                )
        );

        getCompositeDisposable().add(RxEventBus.getInstance().getEvents(Room.class)
                .subscribeOn(schedulerProvider.ui())
                .subscribe(
                        room -> {
                            Log.d("lsc", "LobbyViewModel room info " + ((Room) room).deviceName + ", " + ((Room) room).deviceAddress);
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = ((Room) room).deviceAddress;
                            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d("lsc", "LobbyViewModel connect onSuccess");
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.d("lsc", "LobbyViewModel connect onFailure " + reason);
                                }
                            });
                        }
                ));

    }

    public void discover() {
        Log.d("lsc", "discover");
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("lsc", "LobbyViewModel discoverPeers onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("lsc", "LobbyViewModel discoverPeers onFailure " + reason);
            }

        });
    }

    public void goToRoomInfo() {
        getNavigator().goToRoomInfoActivity();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("lsc", "LobbyViewModel onCleared");
    }
}

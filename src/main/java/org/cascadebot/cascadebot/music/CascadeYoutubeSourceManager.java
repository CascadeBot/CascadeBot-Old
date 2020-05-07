package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeHttpContextFilter;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.lava.extensions.youtuberotator.YoutubeIpRotatorFilter;
import com.sedmelluq.lava.extensions.youtuberotator.planner.RotatingNanoIpRoutePlanner;
import com.sedmelluq.lava.extensions.youtuberotator.tools.ip.IpBlock;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CascadeYoutubeSourceManager extends YoutubeAudioSourceManager {

    private final HttpInterfaceManager httpInterfaceManager;

    public CascadeYoutubeSourceManager(boolean allowSearch, List<InetAddress> addressList) {
        super(allowSearch);
        this.httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
        List<IpBlock<InetAddress>> ipBlocks = new ArrayList<>();
        Random random = new Random();
        ipBlocks.add(new IpBlock<>() {
            @Override
            public InetAddress getRandomAddress() {
                return addressList.get(random.nextInt(addressList.size() - 1));
            }

            @Override
            public Class<InetAddress> getType() {
                return InetAddress.class;
            }

            @Override
            public BigInteger getSize() {
                return BigInteger.valueOf(addressList.size());
            }

            @Override
            public int getMaskBits() {
                return 0;
            }
        });
        this.httpInterfaceManager.setHttpContextFilter(new YoutubeIpRotatorFilter(new YoutubeHttpContextFilter(), true, new RotatingNanoIpRoutePlanner(ipBlocks), 5));
    }

    @Override
    public HttpInterface getHttpInterface() {
        return httpInterfaceManager.getInterface();
    }

}

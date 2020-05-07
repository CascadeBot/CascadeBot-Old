package org.cascadebot.cascadebot.music;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeHttpContextFilter;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.lava.extensions.youtuberotator.YoutubeIpRotatorFilter;
import com.sedmelluq.lava.extensions.youtuberotator.planner.RotatingNanoIpRoutePlanner;
import com.sedmelluq.lava.extensions.youtuberotator.tools.ip.IpBlock;
import com.sedmelluq.lava.extensions.youtuberotator.tools.ip.Ipv4Block;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class CascadeYoutubeSourceManager extends YoutubeAudioSourceManager {

    private final HttpInterfaceManager httpInterfaceManager;

    public CascadeYoutubeSourceManager(boolean allowSearch, List<IpBlock> addressList) {
        super(allowSearch);
        this.httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
        this.httpInterfaceManager.setHttpContextFilter(new YoutubeIpRotatorFilter(new YoutubeHttpContextFilter(), true, new RotatingNanoIpRoutePlanner(addressList), 5));
    }

    @Override
    public HttpInterface getHttpInterface() {
        return httpInterfaceManager.getInterface();
    }

}

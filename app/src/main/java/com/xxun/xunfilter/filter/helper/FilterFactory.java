package com.xxun.xunfilter.filter.helper;

import com.xxun.xunfilter.filter.advanced.AntiqueFilter;
import com.xxun.xunfilter.filter.advanced.BlackCatFilter;
import com.xxun.xunfilter.filter.advanced.CalmFilter;
import com.xxun.xunfilter.filter.advanced.CoolFilter;
import com.xxun.xunfilter.filter.advanced.CrayonFilter;
import com.xxun.xunfilter.filter.advanced.EmeraldFilter;
import com.xxun.xunfilter.filter.advanced.EvergreenFilter;
import com.xxun.xunfilter.filter.advanced.FairytaleFilter;
import com.xxun.xunfilter.filter.advanced.HealthyFilter;
import com.xxun.xunfilter.filter.advanced.LatteFilter;
import com.xxun.xunfilter.filter.advanced.NostalgiaFilter;
import com.xxun.xunfilter.filter.advanced.RomanceFilter;
import com.xxun.xunfilter.filter.advanced.SakuraFilter;
import com.xxun.xunfilter.filter.advanced.SketchFilter;
import com.xxun.xunfilter.filter.advanced.SkinWhitenFilter;
import com.xxun.xunfilter.filter.advanced.SunriseFilter;
import com.xxun.xunfilter.filter.advanced.SunsetFilter;
import com.xxun.xunfilter.filter.advanced.SweetsFilter;
import com.xxun.xunfilter.filter.advanced.TenderFilter;
import com.xxun.xunfilter.filter.advanced.WarmFilter;
import com.xxun.xunfilter.filter.advanced.WhiteCatFilter;
import com.xxun.xunfilter.filter.base.gpuimage.GPUImageFilter;

public class FilterFactory {

    private static FilterType filterType = FilterType.NONE;

    public static GPUImageFilter initFilters(FilterType type) {
        filterType = type;
        switch (type) {
            case WHITECAT:
                return new WhiteCatFilter();
            case BLACKCAT:
                return new BlackCatFilter();
            case SKINWHITEN:
                return new SkinWhitenFilter();
            case ROMANCE:
                return new RomanceFilter();
            case SAKURA:
                return new SakuraFilter();
            case ANTIQUE:
                return new AntiqueFilter();
            case CALM:
                return new CalmFilter();
            case EVERGREEN:
                return new EvergreenFilter();
            case HEALTHY:
                return new HealthyFilter();
            case COOL:
                return new CoolFilter();
            case EMERALD:
                return new EmeraldFilter();
            case LATTE:
                return new LatteFilter();
            case WARM:
                return new WarmFilter();
            case TENDER:
                return new TenderFilter();
            case SWEETS:
                return new SweetsFilter();
            case NOSTALGIA:
                return new NostalgiaFilter();
            case FAIRYTALE:
                return new FairytaleFilter();
            case SUNRISE:
                return new SunriseFilter();
            case SUNSET:
                return new SunsetFilter();
            case CRAYON:
                return new CrayonFilter();
            case SKETCH:
                return new SketchFilter();
            default:
                return null;
        }
    }

    public FilterType getCurrentFilterType() {
        return filterType;
    }
}

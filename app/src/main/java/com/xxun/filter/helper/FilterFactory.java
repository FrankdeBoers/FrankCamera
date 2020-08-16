package com.xxun.filter.helper;

import com.xxun.filter.advanced.AntiqueFilter;
import com.xxun.filter.advanced.BlackCatFilter;
import com.xxun.filter.advanced.CalmFilter;
import com.xxun.filter.advanced.CoolFilter;
import com.xxun.filter.advanced.CrayonFilter;
import com.xxun.filter.advanced.EmeraldFilter;
import com.xxun.filter.advanced.EvergreenFilter;
import com.xxun.filter.advanced.FairytaleFilter;
import com.xxun.filter.advanced.HealthyFilter;
import com.xxun.filter.advanced.LatteFilter;
import com.xxun.filter.advanced.NostalgiaFilter;
import com.xxun.filter.advanced.RomanceFilter;
import com.xxun.filter.advanced.SakuraFilter;
import com.xxun.filter.advanced.SketchFilter;
import com.xxun.filter.advanced.SkinWhitenFilter;
import com.xxun.filter.advanced.SunriseFilter;
import com.xxun.filter.advanced.SunsetFilter;
import com.xxun.filter.advanced.SweetsFilter;
import com.xxun.filter.advanced.TenderFilter;
import com.xxun.filter.advanced.WarmFilter;
import com.xxun.filter.advanced.WhiteCatFilter;
import com.xxun.filter.base.gpuimage.GPUImageFilter;

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

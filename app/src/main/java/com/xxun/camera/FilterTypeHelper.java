package com.xxun.camera;

import com.xxun.filter.helper.FilterType;
import com.frank.filtercamera.R;

public class FilterTypeHelper {

    public static int FilterType2Color(FilterType filterType) {
        switch (filterType) {
            case NONE:
                return R.color.filter_color_grey_light;
            case WHITECAT:
            case BLACKCAT:
            case SUNRISE:
            case SUNSET:
                return R.color.filter_color_brown_light;
            case COOL:
                return R.color.filter_color_blue_dark;
            case EMERALD:
            case EVERGREEN:
                return R.color.filter_color_blue_dark_dark;
            case FAIRYTALE:
                return R.color.filter_color_blue;
            case ROMANCE:
            case SAKURA:
            case WARM:
                return R.color.filter_color_pink;
            case ANTIQUE:
            case NOSTALGIA:
                return R.color.filter_color_green_dark;
            case SKINWHITEN:
            case HEALTHY:
                return R.color.filter_color_red;
            case SWEETS:
                return R.color.filter_color_red_dark;
            case CALM:
            case LATTE:
            case TENDER:
                return R.color.filter_color_brown;
            default:
                return R.color.filter_color_grey_light;
        }
    }

    public static int FilterType2Thumb(FilterType filterType) {
        switch (filterType) {
            case NONE:
                return R.drawable.filter_thumb_original;
            case WHITECAT:
                return R.drawable.filter_thumb_whitecat;
            case BLACKCAT:
                return R.drawable.filter_thumb_blackcat;
            case ROMANCE:
                return R.drawable.filter_thumb_romance;
            case SAKURA:
                return R.drawable.filter_thumb_sakura;
            case ANTIQUE:
                return R.drawable.filter_thumb_antique;
            case SKINWHITEN:
                return R.drawable.filter_thumb_beauty;
            case CALM:
                return R.drawable.filter_thumb_calm;
            case COOL:
                return R.drawable.filter_thumb_cool;
            case EMERALD:
                return R.drawable.filter_thumb_emerald;
            case EVERGREEN:
                return R.drawable.filter_thumb_evergreen;
            case FAIRYTALE:
                return R.drawable.filter_thumb_fairytale;
            case HEALTHY:
                return R.drawable.filter_thumb_healthy;
            case NOSTALGIA:
                return R.drawable.filter_thumb_nostalgia;
            case TENDER:
                return R.drawable.filter_thumb_tender;
            case SWEETS:
                return R.drawable.filter_thumb_sweets;
            case LATTE:
                return R.drawable.filter_thumb_latte;
            case WARM:
                return R.drawable.filter_thumb_warm;
            case SUNRISE:
                return R.drawable.filter_thumb_sunrise;
            case SUNSET:
                return R.drawable.filter_thumb_sunset;
            case CRAYON:
                return R.drawable.filter_thumb_crayon;
            case SKETCH:
                return R.drawable.filter_thumb_sketch;
            default:
                return R.drawable.filter_thumb_original;
        }
    }

    public static int FilterType2Name(FilterType filterType) {
        switch (filterType) {
            case NONE:
                return R.string.filter_none;
            case WHITECAT:
                return R.string.filter_whitecat;
            case BLACKCAT:
                return R.string.filter_blackcat;
            case ROMANCE:
                return R.string.filter_romance;
            case SAKURA:
                return R.string.filter_sakura;
            case ANTIQUE:
                return R.string.filter_antique;
            case CALM:
                return R.string.filter_calm;
            case COOL:
                return R.string.filter_cool;
            case EMERALD:
                return R.string.filter_emerald;
            case EVERGREEN:
                return R.string.filter_evergreen;
            case FAIRYTALE:
                return R.string.filter_fairytale;
            case HEALTHY:
                return R.string.filter_healthy;
            case NOSTALGIA:
                return R.string.filter_nostalgia;
            case TENDER:
                return R.string.filter_tender;
            case SWEETS:
                return R.string.filter_sweets;
            case LATTE:
                return R.string.filter_latte;
            case WARM:
                return R.string.filter_warm;
            case SUNRISE:
                return R.string.filter_sunrise;
            case SUNSET:
                return R.string.filter_sunset;
            case SKINWHITEN:
                return R.string.filter_skinwhiten;
            case CRAYON:
                return R.string.filter_crayon;
            case SKETCH:
                return R.string.filter_sketch;
            default:
                return R.string.filter_none;
        }
    }
}

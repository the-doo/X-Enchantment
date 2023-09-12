package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.enchantment.BaseXEnchantment;

public interface Advable<T extends BaseXEnchantment> {

    TrueTrigger getAdvTrigger();
}

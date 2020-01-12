/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.workstation.process.inventory;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.ItemComponent;

import java.util.ArrayList;
import java.util.List;

public class ItemPrefabPredicateFactory {

    private static final Logger logger = LoggerFactory.getLogger(ItemPrefabPredicateFactory.class);
    private static ItemPrefabPredicateFactory instance;

    // private static
    private static ItemPrefabPredicateFactory getInstance() {
        if (instance == null) {
            instance = new ItemPrefabPredicateFactory();
        }
        return instance;
    }

    // public static
    public static Predicate<EntityRef> get(ResourceUrn urn) {
        return getInstance().getPredicateFor(urn);
    }

    public static void registerProvider(ItemPrefabPredicateProvider provider) {
        getInstance().addProvider(provider);
    }


    // ------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------
    // instance
    private List<ItemPrefabPredicateProvider> providers = new ArrayList<>();

    private Predicate<EntityRef> getPredicateFor(ResourceUrn urn) {
        for (ItemPrefabPredicateProvider provider : providers) {
            if (provider.canProvideForResourceUrn(urn)) {
                return provider.provide(urn);
            }
        }
        return new ItemPrefabPredicate(urn);
    }

    private void addProvider(ItemPrefabPredicateProvider provider) {
        providers.add(provider);
    }

    private static final class ItemPrefabPredicate implements Predicate<EntityRef> {
        private ResourceUrn prefab;

        private ItemPrefabPredicate(ResourceUrn prefab) {
            this.prefab = prefab;
        }

        @Override
        public boolean apply(EntityRef input) {
            ItemComponent item = input.getComponent(ItemComponent.class);
            if (item == null) {
                return false;
            }
            return input.getParentPrefab().getUrn().equals(prefab);
        }
    }
}

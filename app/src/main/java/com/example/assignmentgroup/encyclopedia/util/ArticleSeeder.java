package com.example.assignmentgroup.encyclopedia.util;

import java.util.ArrayList;
import java.util.List;

import com.example.assignmentgroup.encyclopedia.model.Article;

public class ArticleSeeder {

    public static List<Article> seedArticles() {
        List<Article> list = new ArrayList<>();

        list.add(new Article(
                "Refuse First: The Most Powerful R", "Refuse",
                "Why saying no to freebies and unnecessary packaging beats recycling them later.",
                "\"Refuse\" comes before the other Rs because waste you never accept never needs to be " +
                "reduced, reused or recycled. Practical habits: decline single-use freebies (pens, " +
                "flyers, plastic cutlery with takeout), say no to a bag if you're only buying one item, " +
                "and skip unnecessary packaging or add-ons at checkout when a plain option exists.",
                "2 min read"));

        list.add(new Article(
                "5 Easy Ways to Reduce Plastic Use", "Reduce",
                "Simple daily swaps that cut down single-use plastic waste.",
                "1. Carry a reusable water bottle and shopping bags.\n" +
                "2. Choose products with minimal or refillable packaging.\n" +
                "3. Avoid single-use cutlery, straws, and cups.\n" +
                "4. Buy pantry staples in bulk when possible.\n" +
                "5. Repair items instead of replacing them where safe to do so.",
                "3 min read"));

        list.add(new Article(
                "Is a Milk Carton Recyclable?", "Recycle",
                "Why cartons need special handling before they hit the recycling bin.",
                "Milk cartons are usually made of layered paperboard laminated with a thin plastic " +
                "(and sometimes foil) layer, known as Tetra Pak-style packaging. Rinse the carton, " +
                "flatten it, and place it in your paper/carton recycling stream if your local " +
                "facility accepts composite cartons - check your council's guidelines, as not all " +
                "facilities can process the plastic-paper layers together.",
                "2 min read"));

        list.add(new Article(
                "What Does Biodegradable Really Mean?", "Rot",
                "The difference between biodegradable, compostable, and degradable labels.",
                "\"Biodegradable\" means a material can be broken down by micro-organisms, but this can " +
                "take anywhere from weeks to decades depending on conditions. \"Compostable\" is a " +
                "stricter claim - the material breaks down into non-toxic compost within a defined " +
                "time in a composting environment. Always check for a recognised certification " +
                "logo rather than relying on the word alone.",
                "3 min read"));

        list.add(new Article(
                "Starting a Kitchen Compost Bin", "Rot",
                "Turning food scraps into soil instead of landfill methane.",
                "Fruit and veg peels, coffee grounds, eggshells and yard trimmings can go in a compost " +
                "bin instead of the general bin, where organic waste in landfill breaks down without " +
                "oxygen and produces methane. Keep a small countertop caddy for scraps, add \"brown\" " +
                "material (dry leaves, cardboard) to balance \"green\" scraps, and turn the pile " +
                "occasionally for faster, less smelly composting.",
                "2 min read"));

        list.add(new Article(
                "E-waste: Hidden Hazards at Home", "E-waste",
                "Why old electronics shouldn't go in the general waste bin.",
                "Electronic waste can contain heavy metals such as lead, mercury and cadmium, which are " +
                "hazardous if incinerated or left to leach in landfill. Most areas have dedicated " +
                "e-waste drop-off points or manufacturer take-back schemes. Wipe personal data from " +
                "devices before disposal, and consider donating still-working electronics for reuse.",
                "3 min read"));

        list.add(new Article(
                "Repair Before You Replace", "Reuse",
                "How small repairs extend a product's life and cut waste.",
                "Many household items - clothing, furniture, small appliances - fail at a single " +
                "replaceable part. Repair cafes, manufacturer spare-part programmes, and online repair " +
                "guides can help you fix an item rather than discard it, saving money and reducing " +
                "the resources needed to manufacture a replacement.",
                "2 min read"));

        list.add(new Article(
                "Sorting Plastic by Resin Code", "Plastic",
                "What the numbers 1-7 inside the recycling triangle actually mean.",
                "The number inside the chasing-arrows triangle identifies the resin type, e.g. 1 = PET, " +
                "2 = HDPE, 5 = PP. Codes 1 and 2 are the most widely recyclable in most municipal " +
                "systems, while 3 (PVC) and 6 (polystyrene) are accepted far less often. Always rinse " +
                "containers and remove non-plastic parts before recycling.",
                "3 min read"));

        list.add(new Article(
                "Metal Recycling Basics", "Metal",
                "Why aluminium and steel are among the most valuable recyclables.",
                "Aluminium can be recycled indefinitely without losing quality, using around 95% less " +
                "energy than producing new aluminium from ore. Rinse cans before recycling and check " +
                "whether your local scheme wants labels removed. Steel (tin) cans are magnetic and " +
                "also widely accepted.",
                "2 min read"));

        list.add(new Article(
                "Giving Textiles a Second Life", "Reuse",
                "Options beyond the general bin for clothes you no longer wear.",
                "Clean, wearable clothing can be donated to charity shops or community swap events. Worn-out " +
                "textiles that can't be reworn can often be dropped at textile-recycling bins, where " +
                "they are shredded into industrial rags or insulation material instead of going to " +
                "landfill.",
                "2 min read"));

        return list;
    }
}

package me.elaineqheart.auctionHouse.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionLogger {

    private static File logFile;

    static {
        logFile = new File(AuctionHouse.getPlugin().getDataFolder(), "transactions.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logTransaction(String buyer, String seller, String item, double price, int amount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String logEntry = String.format("[%s] Buyer: %s | Seller: %s | Item: %s | Amount: %d | Price: %.2f",
                    timestamp, buyer, seller, item, amount, price);
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

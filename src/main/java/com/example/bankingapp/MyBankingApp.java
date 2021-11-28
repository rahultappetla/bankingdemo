package com.example.bankingapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/banking")
public class MyBankingApp {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    // Ping
    @GetMapping("/ping")
    public Map<String, String> ping() {
        HashMap<String, String> response = new HashMap<>();
        response.put("response", "ping");
        return response;
    }

    // Create Account
    @PostMapping("/accounts")
    public Map<String, String> create_acc(@RequestBody Map<String, String> body){
        String acc_holder = body.get("acc_holder");
        long acc_id = Long.parseLong(body.get("acc_id"));
        float acc_bal = Float.parseFloat(body.get("acc_bal"));
        float acc_loan = Float.parseFloat(body.get("acc_loan"));
        Account acc = new Account(acc_holder, acc_id, acc_bal, acc_loan);
        accountRepository.save(acc);
        return acc.create_account();
    }

    // List All Accounts
    @GetMapping("/accounts")
    public List<Account> get_accounts(){
        return accountRepository.findAll();
    }

    // Get Account Snapshot
    @GetMapping("/account")
    public Optional<Account> get_acc(@RequestBody Map<String, String> body) {
        long acc_id = Long.parseLong(body.get("acc_id"));
        return accountRepository.findById(acc_id);
    }

    @PostMapping("/account/transaction")
    public Map<String, String > post_trans(@RequestBody Map<String, String> body){

        float pre_trans_acc_bal;
        long trans_id = Long.parseLong(body.get("trans_id"));
        long acct_id = Long.parseLong(body.get("acct_id"));
        String trans_type = body.get("trans_type");
        float trans_amount = Float.parseFloat(body.get("trans_amount"));
        String trans_vendor = body.get("trans_vendor");
        String base_type = body.get("base_type");


        Optional<Account> account_held = accountRepository.findById(acct_id);

        if (account_held.isPresent()){
             pre_trans_acc_bal = account_held.get().accBal;
             float loan = account_held.get().accLoan;
             Transaction trns = new Transaction(trans_id, acct_id,trans_type, trans_amount, trans_vendor, base_type, pre_trans_acc_bal);
             Map<String, String> transaction = trns.process_transaction();
             float post_trans_acc_bal = Float.parseFloat(transaction.get("post_trans_acc_bal"));
             account_held.stream().forEach(account -> account.setAccBal(post_trans_acc_bal));
             account_held.stream().forEach(account -> account.setNetBal(post_trans_acc_bal-loan));
             transactionRepository.save(trns);
             return transaction;
        } else {
            Map<String,String> message = null;
            message.put("Message", "Account not found");
            return message;
        }
    }

    // List Funds
    @GetMapping("/account/transaction")
    public String deposit() {
        return "Deposited";
    }

    // Withdraw Funds
    @GetMapping("/withdraw")
    public String withdraw() {
        return "Withdrawal";
    }

    // Get loan
    @GetMapping("/loan")
    public String loan() {
        return "Loan processed";
    }

    // Get Balance
    @GetMapping("/balance")
    public String balance() {
        return "Balance";
    }
}

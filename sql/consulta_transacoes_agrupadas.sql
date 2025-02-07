SELECT t.store_name AS nome,
       t.store_owner AS dono,
       SUM(t.value) AS saldo_total,
       json_agg(
               json_build_object(
                       'id', t.id,
                       'type', json_build_object(
                               'id', CASE t.type
                                         WHEN 'DEBIT' THEN 1
                                         WHEN 'BANK_SLIP' THEN 2
                                         WHEN 'FINANCING' THEN 3
                                         WHEN 'CREDIT' THEN 4
                                         WHEN 'LOAN_RECEIPT' THEN 5
                                         WHEN 'SALES' THEN 6
                                         WHEN 'TED_RECEIPT' THEN 7
                                         WHEN 'DOC_RECEIPT' THEN 8
                                         WHEN 'RENT' THEN 9
                           END,
                               'description', CASE t.type
                                                  WHEN 'DEBIT' THEN 'Débito'
                                                  WHEN 'BANK_SLIP' THEN 'Boleto'
                                                  WHEN 'FINANCING' THEN 'Financiamento'
                                                  WHEN 'CREDIT' THEN 'Crédito'
                                                  WHEN 'LOAN_RECEIPT' THEN 'Recebimento Empréstimo'
                                                  WHEN 'SALES' THEN 'Vendas'
                                                  WHEN 'TED_RECEIPT' THEN 'Recebimento TED'
                                                  WHEN 'DOC_RECEIPT' THEN 'Recebimento DOC'
                                                  WHEN 'RENT' THEN 'Aluguel'
                                                  ELSE 'Desconhecido'
                                   END,
                               'nature', CASE t.type
                                             WHEN 'DEBIT' THEN 'Entrada'
                                             WHEN 'CREDIT' THEN 'Entrada'
                                             WHEN 'LOAN_RECEIPT' THEN 'Entrada'
                                             WHEN 'SALES' THEN 'Entrada'
                                             WHEN 'TED_RECEIPT' THEN 'Entrada'
                                             WHEN 'DOC_RECEIPT' THEN 'Entrada'
                                             ELSE 'Saída'
                                   END,
                               'signal', CASE t.type
                                             WHEN 'DEBIT' THEN '+'
                                             WHEN 'CREDIT' THEN '+'
                                             WHEN 'LOAN_RECEIPT' THEN '+'
                                             WHEN 'SALES' THEN '+'
                                             WHEN 'TED_RECEIPT' THEN '+'
                                             WHEN 'DOC_RECEIPT' THEN '+'
                                             ELSE '-'
                                   END
                               ),
                       'date', t.date,
                       'value', t.value,
                       'cpf', t.cpf,
                       'card', t.card,
                       'hour', t.hour
               )
       ) AS transacoes
FROM transactions t
GROUP BY t.store_name, t.store_owner;
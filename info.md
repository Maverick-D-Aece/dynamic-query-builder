Data Query Language (DQL) primarily revolves around the `SELECT` statement, which is used to query the database and retrieve data. Here are various ways to use the `SELECT` statement with examples:

### 1. Basic `SELECT` Query

Retrieve all columns from a table.

```sql
SELECT * FROM employees;
```

Retrieve specific columns from a table.

```sql
SELECT first_name, last_name, salary FROM employees;
```

### 2. `SELECT` with `WHERE` Clause

Retrieve data based on a condition.

```sql
SELECT first_name, last_name FROM employees WHERE salary > 50000;
```

Retrieve data based on multiple conditions.

```sql
SELECT first_name, last_name FROM employees WHERE department_id = 2 AND salary > 50000;
```

### 3. `SELECT` with `ORDER BY`

Retrieve data sorted in ascending or descending order.

```sql
-- Ascending order by last_name
SELECT first_name, last_name FROM employees ORDER BY last_name ASC;

-- Descending order by salary
SELECT first_name, last_name, salary FROM employees ORDER BY salary DESC;
```

### 4. `SELECT` with `GROUP BY` and Aggregation

Group data and perform aggregate functions like `COUNT`, `SUM`, `AVG`, `MAX`, and `MIN`.

```sql
-- Count the number of employees in each department
SELECT department_id, COUNT(*) AS num_employees FROM employees GROUP BY department_id;

-- Calculate the average salary in each department
SELECT department_id, AVG(salary) AS average_salary FROM employees GROUP BY department_id;
```

### 5. `SELECT` with `HAVING`

Filter groups based on a condition.

```sql
-- Find departments with more than 10 employees
SELECT department_id, COUNT(*) AS num_employees
FROM employees
GROUP BY department_id
HAVING COUNT(*) > 10;

-- Find departments with average salary greater than 60000
SELECT department_id, AVG(salary) AS average_salary
FROM employees
GROUP BY department_id
HAVING AVG(salary) > 60000;
```

### 6. `SELECT` with Joins

Combine rows from two or more tables based on a related column.

```sql
-- Inner join between employees and departments
SELECT employees.first_name, employees.last_name, departments.department_name
FROM employees
JOIN departments ON employees.department_id = departments.department_id;

-- Left join between employees and departments
SELECT employees.first_name, employees.last_name, departments.department_name
FROM employees
LEFT JOIN departments ON employees.department_id = departments.department_id;

-- Right join between employees and departments
SELECT employees.first_name, employees.last_name, departments.department_name
FROM employees
RIGHT JOIN departments ON employees.department_id = departments.department_id;
```

### 7. `SELECT` with Subqueries

Use a query inside another query.

```sql
-- Subquery in WHERE clause
SELECT first_name, last_name FROM employees
WHERE department_id IN (SELECT department_id FROM departments WHERE location_id = 1);

-- Subquery in SELECT clause
SELECT first_name, last_name, (SELECT department_name FROM departments WHERE departments.department_id = employees.department_id) AS department_name
FROM employees;
```

### 8. `SELECT DISTINCT`

Retrieve unique values.

```sql
-- Select distinct departments
SELECT DISTINCT department_id FROM employees;

-- Select distinct job titles
SELECT DISTINCT job_title FROM employees;
```

### 9. `SELECT` with Aliases

Rename columns or tables for readability.

```sql
-- Column alias
SELECT first_name AS fname, last_name AS lname, salary AS sal FROM employees;

-- Table alias
SELECT e.first_name, e.last_name, d.department_name
FROM employees AS e
JOIN departments AS d ON e.department_id = d.department_id;
```

### 10. `SELECT` with Limit and Offset

Limit the number of rows returned and skip a number of rows.

```sql
-- Limit the number of rows to 10
SELECT * FROM employees LIMIT 10;

-- Skip the first 5 rows and return the next 10 rows
SELECT * FROM employees LIMIT 10 OFFSET 5;
```

These examples cover a wide range of ways to use the `SELECT` statement in SQL, providing a foundation for querying databases effectively.

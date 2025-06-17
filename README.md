# 📊 UniUsage CLI Tool

**UniUsage** is a command-line tool for analyzing user behavior from log files. It provides three core functionalities:

- ✅ Estimate unique user counts per operation using **HyperLogLog** (for large datasets)
- 🔍 Brute-force count of unique users per operation (for small datasets)
- 🔢 Count total number of log entries with multithreading

---

## 🚀 Installation

> **Requirement:** Java **17** or higher

### Build using Gradle

```bash
./gradlew build
```

### Run the CLI

```bash
java -jar build/libs/UniUsage.jar [command] [options]
```

---

## ⚙️ Available Commands

### 🔹 `tophll`

Estimate top K operations by unique user count using **HyperLogLog**.

**Usage:**

```bash
java -jar build/libs/UniUsage.jar tophll --logfile <path_to_log_file> [--k <top_k>] [--precision <hll_precision>]
```

**Options:**

- `--logfile` (required): Path to the log file  
- `--k`, `--top-k`: Number of top operations to display (default: `2`)  
- `--precision`, `--hll-precision`: HLL precision (default: `18`, ~0.2% standard error)

---

### 🔹 `top`

Brute-force method to find top K operations by unique users.  
**Best for small datasets.**

**Usage:**

```bash
java -jar build/libs/UniUsage.jar top --logfile <path_to_log_file> [--k <top_k>]
```

**Options:**

- `--logfile` (required): Path to the log file  
- `--k`, `--top-k`: Number of top operations to display (default: `2`)

---

### 🔹 `count`

Multithreaded log processor to count **total number of entries** in the log file.

**Usage:**

```bash
java -jar build/libs/UniUsage.jar count --logfile <path_to_log_file>
```

**Options:**

- `--logfile` (required): Path to the log file

---

## 🧠 Problem Overview

We are given a log file containing user actions and asked to:

1. Identify the **top K most used operations**.
2. Determine the **percentage of users** who used each operation.
3. Ensure **each user is counted once per operation**.

---

## 🧩 Problem 1 — Finding Top K Operations

> 🧑‍💼 "Can you tell me the most used two operations and the % of users who use them?"
>
> ✅ Your response: "Consider it done. Each user will be counted only once per operation."

### ✅ Two Implemented Solutions

#### 🔬 1. HyperLogLog Algorithm (Recommended for Large Logs)

```bash
java -jar build/libs/UniUsage.jar tophll --logfile log_file.log
```

**Sample Output:**

```
Top 2 operations by unique users with standard error of 0.20%:

Operation "connect" is used by 81.23% of our users (2251 users).
Operation "filter-changed" is used by 80.01% of our users (2217 users).
```

> HLL provides high performance and low memory usage with a small error margin (±0.2%).

---

#### 🛠️ 2. Brute Force Algorithm (Accurate for Small/Medium Logs)

```bash
java -jar build/libs/UniUsage.jar top --logfile log_file.log
```

**Sample Output:**

```
Operation "connect" is used by 81.23% of our users (2251 users).
Operation "filter-changed" is used by 80.04% of our users (2218 users).
```

> Ideal for debugging, development, and small-scale datasets.

---

## 🧪 Problem 2 — How Do You Verify It Works?

> 🧑‍💼 "How do you know your results are correct?"

- ✅ I created smaller sample logs like `sm_log_file_x.log`
- 🔎 Manually analyzed them to find expected top operations and percentages
- 🧪 Ran both brute-force and HLL implementations on the samples
- ✅ Verified the outputs matched the expected results

---

## 🔄 Problem 3 — Changing K Easily

> 🧑‍💼 "Can I find the top 3 operations instead of just 2?"

Absolutely. Use the `--top-k` flag:

```bash
java -jar build/libs/UniUsage.jar top --logfile log_file.log --top-k 3
```

**Sample Output:**

```
Operation "connect" is used by 81.23% of our users (2251 users).
Operation "filter-changed" is used by 80.04% of our users (2218 users).
Operation "open-detailed-quotes" is used by 45.11% of our users (1250 users).
```

> The CLI is flexible — change `--top-k` to any number you need.

---

## 🚄 Problem 4 — Why is it Fast or Slow?

> 🧑‍💼 "Why is it slow? Can you make it faster?"

Yes, performance was part of the design. Here's how it’s optimized:

- 🧵 **Multithreading** is used for counting entries (`count` command)
- 🔍 **Brute force** for accuracy in small logs
- 🌀 **HyperLogLog** for large logs with minimal memory overhead
- ⚙️ Easy to switch between them using CLI flags

---


CREATE DATABASE IF NOT EXISTS recipe_discovery
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE recipe_discovery;

-----------------------------------------------------
-- USERS
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  avatar_url VARCHAR(500) DEFAULT 'https://ui-avatars.com/api/?name=User&background=4caf50&color=fff',
  note VARCHAR(255) NULL,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- CATEGORIES (hệ thống)
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  color_code VARCHAR(50) NOT NULL DEFAULT '#4caf50',
  icon VARCHAR(50) DEFAULT 'utensils',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- USER CUSTOM CATEGORIES
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS user_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    color_code VARCHAR(20),
    icon VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_category_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- RECIPES
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS recipes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  user_category_id BIGINT NULL,
  category_id BIGINT NULL,
  title VARCHAR(150) NOT NULL,
  ingredients TEXT NOT NULL,
  instructions TEXT NOT NULL,
  calories INT DEFAULT 0,
  cooking_time INT DEFAULT 0,
  servings INT DEFAULT 1,
  image_url TEXT,
  is_public TINYINT(1) NOT NULL DEFAULT 0,

  status VARCHAR(20) DEFAULT NULL,
  rejected_reason TEXT,

  share_status VARCHAR(20) NULL,
  share_approved_at DATETIME NULL,
  share_rejected_reason TEXT NULL,

  version INT NOT NULL DEFAULT 0,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_recipes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_recipes_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
  CONSTRAINT fk_recipes_user_category FOREIGN KEY (user_category_id) REFERENCES user_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- FAVORITES
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  recipe_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_fav (user_id, recipe_id),
  CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_fav_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- MEAL PLANS
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS meal_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    week_start_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY unique_user_week (user_id, week_start_date),
    CONSTRAINT fk_meal_plan_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- MEAL PLAN ITEMS
-----------------------------------------------------
CREATE TABLE IF NOT EXISTS meal_plan_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meal_plan_id BIGINT NOT NULL,
    day_of_week INT NOT NULL,               
    meal_type VARCHAR(20) NOT NULL,         
    recipe_id BIGINT NULL,                  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_item_plan FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-----------------------------------------------------
-- INSERT DEFAULT ADMIN
-----------------------------------------------------
INSERT INTO users (full_name, email, password, role)
VALUES ('Admin', 'admin@recipe.com', 'admin123', 'ADMIN');
